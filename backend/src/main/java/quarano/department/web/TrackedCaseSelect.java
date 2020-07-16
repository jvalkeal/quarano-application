package quarano.department.web;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.*;

import lombok.RequiredArgsConstructor;
import quarano.department.TrackedCase;

import java.time.format.DateTimeFormatter;

import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

/**
 * Trimmed down representation to be used from selection dialogues that basically need a link to a case by the person's
 * name.
 *
 * @author Oliver Drotbohm
 */
@Relation(collectionRelation = "cases")
@RequiredArgsConstructor(staticName = "of")
public class TrackedCaseSelect extends RepresentationModel<TrackedCaseSelect> {

	private final TrackedCase trackedCase;

	public String getFirstName() {
		return trackedCase.getTrackedPerson().getFirstName();
	}

	public String getLastName() {
		return trackedCase.getTrackedPerson().getLastName();
	}

	public String getDateOfBirth() {

		var dateOfBirth = trackedCase.getTrackedPerson().getDateOfBirth();

		return dateOfBirth == null ? null : dateOfBirth.format(DateTimeFormatter.ISO_DATE);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.hateoas.RepresentationModel#getLinks()
	 */
	@Override
	public Links getLinks() {
		return super.getLinks().and(createSelfLink(IanaLinkRelations.SELF));
	}

	private Link createSelfLink(LinkRelation relation) {
		var caseLink = on(TrackedCaseController.class).getCase(trackedCase.getId(), trackedCase.getDepartment());
		return Link.of(fromMethodCall(caseLink).toUriString(), relation);
	}
}
