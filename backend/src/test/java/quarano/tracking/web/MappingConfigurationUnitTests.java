/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package quarano.tracking.web;

import static org.assertj.core.api.Assertions.*;

import de.wevsvirushackathon.coronareport.firstReport.FirstReport;
import quarano.department.InitialReport;
import quarano.tracking.Address;
import quarano.tracking.Address.HouseNumber;
import quarano.tracking.TrackedPerson;
import quarano.tracking.ZipCode;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.modelmapper.ModelMapper;

/**
 * @author Oliver Drotbohm
 */
@TestInstance(Lifecycle.PER_CLASS)
public class MappingConfigurationUnitTests {

	ModelMapper mapper = new ModelMapper();

	@BeforeAll
	void setUp() {
		new MappingConfiguration(mapper);
	}

	@Test
	void mapsFirstReportToInitialReport() {

		var report = new FirstReport();
		report.setNursingActionOnC19Pat(true);

		InitialReport result = mapper.map(report, InitialReport.class);

		assertThat(result.getNursingActionOnC19Pat()).isTrue();
	}

	@Test
	void mapsTrackedPersonDtoToEntity() {

		TrackedPersonDto dto = new TrackedPersonDto();
		dto.setFirstname("Firstname");
		dto.setSurename("Lastname");
		dto.setStreet("Street");
		dto.setHouseNumber("4711");
		dto.setCity("City");
		dto.setZipCode("01234");

		TrackedPerson result = mapper.map(dto, TrackedPerson.class);

		assertThat(result.getFirstname()).isEqualTo(dto.getFirstname());
		assertThat(result.getLastname()).isEqualTo(dto.getSurename());

		Address address = result.getAddress();

		assertThat(address.getStreet()).isEqualTo(dto.getStreet());
		assertThat(address.getHouseNumber()).isEqualTo(HouseNumber.of(dto.getHouseNumber()));
		assertThat(address.getCity()).isEqualTo(dto.getCity());
		assertThat(address.getZipCode()).isEqualTo(ZipCode.of(dto.getZipCode()));
	}
}
