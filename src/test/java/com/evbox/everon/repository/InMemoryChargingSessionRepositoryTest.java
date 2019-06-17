package com.evbox.everon.repository;

import com.evbox.everon.model.ChargingSession;
import com.evbox.everon.model.StatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.time.LocalDateTime.now;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InMemoryChargingSessionRepositoryTest {

    private ChargingSessionRepository repository;

    @BeforeEach
    public void init() {
        repository = new InMemoryChargingSessionRepository();
    }

    @Test
    @DisplayName("Save should save charging session")
    public void shouldSaveChargingSession() {

        //given
        ChargingSession chargingSession = createChargingSession(UUID.randomUUID());

        //when
        ChargingSession actual = repository.save(chargingSession);

        //then
        assertThat(actual, equalTo(chargingSession));
    }

    @Test
    @DisplayName("Save should throw Exception if charging session is null")
    public void shouldThrowNullPointerExceptionWhenNull() {

        // when, then
        assertThrows(NullPointerException.class,
                () -> repository.save(null)
        );
    }

    @Test
    @DisplayName("FindById Should find charging session by id")
    public void shouldFindById() {

        //given
        UUID id = UUID.randomUUID();

        ChargingSession chargingSession = createChargingSession(id);
        repository.save(chargingSession);

        //when
        Optional<ChargingSession> actual = repository.findById(id);

        //then
        assertThat("isPresent", actual.isPresent());
        assertThat(actual.get(), equalTo(chargingSession));
    }

    @Test
    @DisplayName("FindById should return empty optional if given id is not present")
    public void shouldReturnEmptyOptionalIfIdIsNotPresent() {

        //given
        ChargingSession chargingSession = createChargingSession(UUID.randomUUID());
        repository.save(chargingSession);

        //when
        Optional<ChargingSession> actual = repository.findById(UUID.randomUUID());

        //then
        assertThat(actual, is(Optional.empty()));
    }

    @Test
    @DisplayName("FindById should throw Exception if id is null")
    public void shouldThrowNullPointerExceptionIfIdIsNull() {

        // when, then
        assertThrows(NullPointerException.class,
                () -> repository.findById(null)
        );
    }

    @Test
    @DisplayName("FindAll should return empty list when repository is empty")
    public void shouldReturnEmptyListIfRepoIsEmpty() {

        //when
        List<ChargingSession> all = repository.findAll();

        //then
        assertThat(all, empty());
    }

    @Test
    @DisplayName("FindAll should return one item list when repository holds one item")
    public void shouldReturnOneItemListIfRepoHasOneItem() {

        //given
        ChargingSession chargingSession = createChargingSession(UUID.randomUUID());
        repository.save(chargingSession);

        //when
        List<ChargingSession> all = repository.findAll();

        //then
        assertThat(all, hasSize(1));
        assertThat(all.get(0), equalTo(chargingSession));
    }

    @Test
    @DisplayName("FindAll should return two items when repository holds two items")
    public void shouldReturnTwoItemsIfRepoHasTwoItems() {

        //given
        repository.save(createChargingSession(UUID.randomUUID()));
        repository.save(createChargingSession(UUID.randomUUID()));

        //when
        List<ChargingSession> all = repository.findAll();

        //then
        assertThat(all, hasSize(2));
    }

    private ChargingSession createChargingSession(UUID id) {
        ChargingSession chargingSession = new ChargingSession();
        chargingSession.setId(id);
        chargingSession.setStationId("EV-1234");
        chargingSession.setStartedAt(now());
        chargingSession.setStatus(StatusEnum.IN_PROGRESS);
        return chargingSession;
    }

}
