package com.mariovn.eventmanager.web.rest;

import com.mariovn.eventmanager.EventmanagerApp;
import com.mariovn.eventmanager.domain.Participant;
import com.mariovn.eventmanager.domain.Expense;
import com.mariovn.eventmanager.domain.Event;
import com.mariovn.eventmanager.domain.UserExtra;
import com.mariovn.eventmanager.repository.ParticipantRepository;
import com.mariovn.eventmanager.service.ParticipantService;
import com.mariovn.eventmanager.service.dto.ParticipantDTO;
import com.mariovn.eventmanager.service.mapper.ParticipantMapper;
import com.mariovn.eventmanager.service.dto.ParticipantCriteria;
import com.mariovn.eventmanager.service.ParticipantQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mariovn.eventmanager.domain.enumeration.ParticipantType;
/**
 * Integration tests for the {@link ParticipantResource} REST controller.
 */
@SpringBootTest(classes = EventmanagerApp.class)

@AutoConfigureMockMvc
@WithMockUser
public class ParticipantResourceIT {

    private static final ParticipantType DEFAULT_TYPE = ParticipantType.OWNER;
    private static final ParticipantType UPDATED_TYPE = ParticipantType.MEMBER;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private ParticipantMapper participantMapper;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ParticipantQueryService participantQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restParticipantMockMvc;

    private Participant participant;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Participant createEntity(EntityManager em) {
        Participant participant = new Participant()
            .type(DEFAULT_TYPE);
        return participant;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Participant createUpdatedEntity(EntityManager em) {
        Participant participant = new Participant()
            .type(UPDATED_TYPE);
        return participant;
    }

    @BeforeEach
    public void initTest() {
        participant = createEntity(em);
    }

    @Test
    @Transactional
    public void createParticipant() throws Exception {
        int databaseSizeBeforeCreate = participantRepository.findAll().size();

        // Create the Participant
        ParticipantDTO participantDTO = participantMapper.toDto(participant);
        restParticipantMockMvc.perform(post("/api/participants")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(participantDTO)))
            .andExpect(status().isCreated());

        // Validate the Participant in the database
        List<Participant> participantList = participantRepository.findAll();
        assertThat(participantList).hasSize(databaseSizeBeforeCreate + 1);
        Participant testParticipant = participantList.get(participantList.size() - 1);
        assertThat(testParticipant.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    public void createParticipantWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = participantRepository.findAll().size();

        // Create the Participant with an existing ID
        participant.setId(1L);
        ParticipantDTO participantDTO = participantMapper.toDto(participant);

        // An entity with an existing ID cannot be created, so this API call must fail
        restParticipantMockMvc.perform(post("/api/participants")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(participantDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Participant in the database
        List<Participant> participantList = participantRepository.findAll();
        assertThat(participantList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = participantRepository.findAll().size();
        // set the field null
        participant.setType(null);

        // Create the Participant, which fails.
        ParticipantDTO participantDTO = participantMapper.toDto(participant);

        restParticipantMockMvc.perform(post("/api/participants")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(participantDTO)))
            .andExpect(status().isBadRequest());

        List<Participant> participantList = participantRepository.findAll();
        assertThat(participantList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllParticipants() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList
        restParticipantMockMvc.perform(get("/api/participants?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(participant.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }
    
    @Test
    @Transactional
    public void getParticipant() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get the participant
        restParticipantMockMvc.perform(get("/api/participants/{id}", participant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(participant.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }


    @Test
    @Transactional
    public void getParticipantsByIdFiltering() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        Long id = participant.getId();

        defaultParticipantShouldBeFound("id.equals=" + id);
        defaultParticipantShouldNotBeFound("id.notEquals=" + id);

        defaultParticipantShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultParticipantShouldNotBeFound("id.greaterThan=" + id);

        defaultParticipantShouldBeFound("id.lessThanOrEqual=" + id);
        defaultParticipantShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllParticipantsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where type equals to DEFAULT_TYPE
        defaultParticipantShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the participantList where type equals to UPDATED_TYPE
        defaultParticipantShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllParticipantsByTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where type not equals to DEFAULT_TYPE
        defaultParticipantShouldNotBeFound("type.notEquals=" + DEFAULT_TYPE);

        // Get all the participantList where type not equals to UPDATED_TYPE
        defaultParticipantShouldBeFound("type.notEquals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllParticipantsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultParticipantShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the participantList where type equals to UPDATED_TYPE
        defaultParticipantShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllParticipantsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        // Get all the participantList where type is not null
        defaultParticipantShouldBeFound("type.specified=true");

        // Get all the participantList where type is null
        defaultParticipantShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    public void getAllParticipantsByExpensesIsEqualToSomething() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);
        Expense expenses = ExpenseResourceIT.createEntity(em);
        em.persist(expenses);
        em.flush();
        participant.addExpenses(expenses);
        participantRepository.saveAndFlush(participant);
        Long expensesId = expenses.getId();

        // Get all the participantList where expenses equals to expensesId
        defaultParticipantShouldBeFound("expensesId.equals=" + expensesId);

        // Get all the participantList where expenses equals to expensesId + 1
        defaultParticipantShouldNotBeFound("expensesId.equals=" + (expensesId + 1));
    }


    @Test
    @Transactional
    public void getAllParticipantsByEventIsEqualToSomething() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);
        Event event = EventResourceIT.createEntity(em);
        em.persist(event);
        em.flush();
        participant.setEvent(event);
        participantRepository.saveAndFlush(participant);
        Long eventId = event.getId();

        // Get all the participantList where event equals to eventId
        defaultParticipantShouldBeFound("eventId.equals=" + eventId);

        // Get all the participantList where event equals to eventId + 1
        defaultParticipantShouldNotBeFound("eventId.equals=" + (eventId + 1));
    }


    @Test
    @Transactional
    public void getAllParticipantsByUserExtraIsEqualToSomething() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);
        UserExtra userExtra = UserExtraResourceIT.createEntity(em);
        em.persist(userExtra);
        em.flush();
        participant.setUserExtra(userExtra);
        participantRepository.saveAndFlush(participant);
        Long userExtraId = userExtra.getId();

        // Get all the participantList where userExtra equals to userExtraId
        defaultParticipantShouldBeFound("userExtraId.equals=" + userExtraId);

        // Get all the participantList where userExtra equals to userExtraId + 1
        defaultParticipantShouldNotBeFound("userExtraId.equals=" + (userExtraId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultParticipantShouldBeFound(String filter) throws Exception {
        restParticipantMockMvc.perform(get("/api/participants?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(participant.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));

        // Check, that the count call also returns 1
        restParticipantMockMvc.perform(get("/api/participants/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultParticipantShouldNotBeFound(String filter) throws Exception {
        restParticipantMockMvc.perform(get("/api/participants?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restParticipantMockMvc.perform(get("/api/participants/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingParticipant() throws Exception {
        // Get the participant
        restParticipantMockMvc.perform(get("/api/participants/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateParticipant() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        int databaseSizeBeforeUpdate = participantRepository.findAll().size();

        // Update the participant
        Participant updatedParticipant = participantRepository.findById(participant.getId()).get();
        // Disconnect from session so that the updates on updatedParticipant are not directly saved in db
        em.detach(updatedParticipant);
        updatedParticipant
            .type(UPDATED_TYPE);
        ParticipantDTO participantDTO = participantMapper.toDto(updatedParticipant);

        restParticipantMockMvc.perform(put("/api/participants")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(participantDTO)))
            .andExpect(status().isOk());

        // Validate the Participant in the database
        List<Participant> participantList = participantRepository.findAll();
        assertThat(participantList).hasSize(databaseSizeBeforeUpdate);
        Participant testParticipant = participantList.get(participantList.size() - 1);
        assertThat(testParticipant.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void updateNonExistingParticipant() throws Exception {
        int databaseSizeBeforeUpdate = participantRepository.findAll().size();

        // Create the Participant
        ParticipantDTO participantDTO = participantMapper.toDto(participant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParticipantMockMvc.perform(put("/api/participants")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(participantDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Participant in the database
        List<Participant> participantList = participantRepository.findAll();
        assertThat(participantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteParticipant() throws Exception {
        // Initialize the database
        participantRepository.saveAndFlush(participant);

        int databaseSizeBeforeDelete = participantRepository.findAll().size();

        // Delete the participant
        restParticipantMockMvc.perform(delete("/api/participants/{id}", participant.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Participant> participantList = participantRepository.findAll();
        assertThat(participantList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
