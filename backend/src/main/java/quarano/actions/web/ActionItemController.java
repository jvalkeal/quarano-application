package quarano.actions.web;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import quarano.account.Department;
import quarano.account.Department.DepartmentIdentifier;
import quarano.actions.ActionItemRepository;
import quarano.actions.ActionItemsManagement;
import quarano.core.web.ErrorsDto;
import quarano.core.web.LoggedIn;
import quarano.department.TrackedCase;
import quarano.department.TrackedCase.TrackedCaseIdentifier;
import quarano.department.TrackedCaseRepository;
import quarano.department.web.ExternalTrackedCaseRepresentations;
import quarano.department.web.TrackedCaseLinkRelations;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Oliver Drotbohm
 */
@RestController
@RequiredArgsConstructor
public class ActionItemController {

	private final @NonNull ActionItemRepository items;
	private final @NonNull ActionItemsManagement actionItems;
	private final @NonNull MessageSourceAccessor messages;
	private final @NonNull TrackedCaseRepository cases;
	private final @NonNull ExternalTrackedCaseRepresentations trackedCaseRepresentations;

	@GetMapping("/api/hd/actions/{identifier}")
	HttpEntity<?> allActions(@PathVariable TrackedCaseIdentifier identifier,
			@LoggedIn DepartmentIdentifier department) {

		var trackedCase = cases.findById(identifier)
				.filter(it -> it.belongsTo(department))
				.orElse(null);

		if (trackedCase == null) {
			return ResponseEntity.notFound().build();
		}

		var id = trackedCase.getTrackedPerson().getId();

		return ResponseEntity.ok(CaseActionsRepresentation.of(trackedCase, items.findByTrackedPerson(id), messages));
	}

	@PutMapping("/api/hd/actions/{identifier}/resolve")
	HttpEntity<?> resolveActions(@PathVariable TrackedCaseIdentifier identifier,
			@Valid @RequestBody ActionsReviewed payload,
			Errors errors,
			@LoggedIn DepartmentIdentifier department) {

		TrackedCase trackedCase = cases.findById(identifier)
				.filter(it -> it.belongsTo(department))
				.orElse(null);

		if (trackedCase == null) {
			return ResponseEntity.notFound().build();
		}

		if (errors.hasErrors()) {
			return ErrorsDto.of(errors, messages).toBadRequest();
		}

		actionItems.resolveItemsFor(trackedCase, payload.getComment());

		return allActions(identifier, department);
	}

	// @GetMapping("/api/hd/actions")
	// Stream<?> getActions(@LoggedIn Department department) {
	//
	// return cases.findByDepartmentId(department.getId())
	// .map(trackedCase -> {
	//
	// var summary = trackedCaseRepresentations.toSummary(trackedCase);
	//
	// return new CaseActionSummary(trackedCase, items.findUnresolvedByActiveCase(trackedCase), summary);
	//
	// })
	// .stream()
	// .filter(CaseActionSummary::hasUnresolvedItems)
	// .sorted(Comparator.comparing(CaseActionSummary::getPriority).reversed());
	// }

	@GetMapping("/api/hd/actions")
	Stream<RepresentationModel<?>> getActions(@LoggedIn Department department) {

		var actions = cases.findByDepartmentId(department.getId())
				.map(this::toSummary)
				.stream()
				// .filter(CaseActionSummary::hasUnresolvedItems)
				// .sorted(Comparator.comparing(CaseActionSummary::getPriority).reversed())
				.collect(Collectors.toUnmodifiableList());

		return actions.stream().map(this::toSummaryRepresentation);
		// return HalModelBuilder.emptyHalModel()
		// .embed(actions, CaseActionSummary.class)
		// .build();
	}

	public CaseActionSummary toSummary(TrackedCase trackedCase) {
		var summary = trackedCaseRepresentations.toSummary(trackedCase);
		return new CaseActionSummary(trackedCase, items.findUnresolvedByActiveCase(trackedCase), summary);
	}

	public RepresentationModel<?> toSummaryRepresentation(CaseActionSummary caseActionSummary) {

		var halModelBuilder = HalModelBuilder.halModelOf(caseActionSummary);
		var originCases = caseActionSummary.getTrackedCase()
				.getOriginCases()
				.stream()
				.map(trackedCaseRepresentations::toSelect)
				.collect(Collectors.toUnmodifiableList());

		if (!originCases.isEmpty()) {
			halModelBuilder.embed(originCases, TrackedCaseLinkRelations.ORIGIN_CASES);
		}

		return halModelBuilder.build();
	}
}
