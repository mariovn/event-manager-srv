package com.mariovn.eventmanager.web.rest;

import com.mariovn.eventmanager.EventmanagerApp;
import com.mariovn.eventmanager.domain.Expense;
import com.mariovn.eventmanager.domain.Event;
import com.mariovn.eventmanager.domain.Participant;
import com.mariovn.eventmanager.repository.ExpenseRepository;
import com.mariovn.eventmanager.service.ExpenseService;
import com.mariovn.eventmanager.service.dto.ExpenseDTO;
import com.mariovn.eventmanager.service.mapper.ExpenseMapper;
import com.mariovn.eventmanager.service.dto.ExpenseCriteria;
import com.mariovn.eventmanager.service.ExpenseQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mariovn.eventmanager.domain.enumeration.ExpenseState;
import com.mariovn.eventmanager.domain.enumeration.CurrencyType;
/**
 * Integration tests for the {@link ExpenseResource} REST controller.
 */
@SpringBootTest(classes = EventmanagerApp.class)

@AutoConfigureMockMvc
@WithMockUser
public class ExpenseResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final ExpenseState DEFAULT_STATE = ExpenseState.NEW;
    private static final ExpenseState UPDATED_STATE = ExpenseState.PENDING;

    private static final Float DEFAULT_COST = 1F;
    private static final Float UPDATED_COST = 2F;
    private static final Float SMALLER_COST = 1F - 1F;

    private static final Float DEFAULT_ORIGINAL_COST = 1F;
    private static final Float UPDATED_ORIGINAL_COST = 2F;
    private static final Float SMALLER_ORIGINAL_COST = 1F - 1F;

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final CurrencyType DEFAULT_CURRENCY_TYPE = CurrencyType.EUR;
    private static final CurrencyType UPDATED_CURRENCY_TYPE = CurrencyType.USD;

    private static final byte[] DEFAULT_TICKET = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_TICKET = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_TICKET_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_TICKET_CONTENT_TYPE = "image/png";

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ExpenseMapper expenseMapper;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private ExpenseQueryService expenseQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restExpenseMockMvc;

    private Expense expense;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Expense createEntity(EntityManager em) {
        Expense expense = new Expense()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .state(DEFAULT_STATE)
            .cost(DEFAULT_COST)
            .originalCost(DEFAULT_ORIGINAL_COST)
            .date(DEFAULT_DATE)
            .currencyType(DEFAULT_CURRENCY_TYPE)
            .ticket(DEFAULT_TICKET)
            .ticketContentType(DEFAULT_TICKET_CONTENT_TYPE);
        return expense;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Expense createUpdatedEntity(EntityManager em) {
        Expense expense = new Expense()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .state(UPDATED_STATE)
            .cost(UPDATED_COST)
            .originalCost(UPDATED_ORIGINAL_COST)
            .date(UPDATED_DATE)
            .currencyType(UPDATED_CURRENCY_TYPE)
            .ticket(UPDATED_TICKET)
            .ticketContentType(UPDATED_TICKET_CONTENT_TYPE);
        return expense;
    }

    @BeforeEach
    public void initTest() {
        expense = createEntity(em);
    }

    @Test
    @Transactional
    public void createExpense() throws Exception {
        int databaseSizeBeforeCreate = expenseRepository.findAll().size();

        // Create the Expense
        ExpenseDTO expenseDTO = expenseMapper.toDto(expense);
        restExpenseMockMvc.perform(post("/api/expenses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(expenseDTO)))
            .andExpect(status().isCreated());

        // Validate the Expense in the database
        List<Expense> expenseList = expenseRepository.findAll();
        assertThat(expenseList).hasSize(databaseSizeBeforeCreate + 1);
        Expense testExpense = expenseList.get(expenseList.size() - 1);
        assertThat(testExpense.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testExpense.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testExpense.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testExpense.getCost()).isEqualTo(DEFAULT_COST);
        assertThat(testExpense.getOriginalCost()).isEqualTo(DEFAULT_ORIGINAL_COST);
        assertThat(testExpense.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testExpense.getCurrencyType()).isEqualTo(DEFAULT_CURRENCY_TYPE);
        assertThat(testExpense.getTicket()).isEqualTo(DEFAULT_TICKET);
        assertThat(testExpense.getTicketContentType()).isEqualTo(DEFAULT_TICKET_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createExpenseWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = expenseRepository.findAll().size();

        // Create the Expense with an existing ID
        expense.setId(1L);
        ExpenseDTO expenseDTO = expenseMapper.toDto(expense);

        // An entity with an existing ID cannot be created, so this API call must fail
        restExpenseMockMvc.perform(post("/api/expenses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(expenseDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Expense in the database
        List<Expense> expenseList = expenseRepository.findAll();
        assertThat(expenseList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = expenseRepository.findAll().size();
        // set the field null
        expense.setName(null);

        // Create the Expense, which fails.
        ExpenseDTO expenseDTO = expenseMapper.toDto(expense);

        restExpenseMockMvc.perform(post("/api/expenses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(expenseDTO)))
            .andExpect(status().isBadRequest());

        List<Expense> expenseList = expenseRepository.findAll();
        assertThat(expenseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCostIsRequired() throws Exception {
        int databaseSizeBeforeTest = expenseRepository.findAll().size();
        // set the field null
        expense.setCost(null);

        // Create the Expense, which fails.
        ExpenseDTO expenseDTO = expenseMapper.toDto(expense);

        restExpenseMockMvc.perform(post("/api/expenses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(expenseDTO)))
            .andExpect(status().isBadRequest());

        List<Expense> expenseList = expenseRepository.findAll();
        assertThat(expenseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkOriginalCostIsRequired() throws Exception {
        int databaseSizeBeforeTest = expenseRepository.findAll().size();
        // set the field null
        expense.setOriginalCost(null);

        // Create the Expense, which fails.
        ExpenseDTO expenseDTO = expenseMapper.toDto(expense);

        restExpenseMockMvc.perform(post("/api/expenses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(expenseDTO)))
            .andExpect(status().isBadRequest());

        List<Expense> expenseList = expenseRepository.findAll();
        assertThat(expenseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = expenseRepository.findAll().size();
        // set the field null
        expense.setDate(null);

        // Create the Expense, which fails.
        ExpenseDTO expenseDTO = expenseMapper.toDto(expense);

        restExpenseMockMvc.perform(post("/api/expenses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(expenseDTO)))
            .andExpect(status().isBadRequest());

        List<Expense> expenseList = expenseRepository.findAll();
        assertThat(expenseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCurrencyTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = expenseRepository.findAll().size();
        // set the field null
        expense.setCurrencyType(null);

        // Create the Expense, which fails.
        ExpenseDTO expenseDTO = expenseMapper.toDto(expense);

        restExpenseMockMvc.perform(post("/api/expenses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(expenseDTO)))
            .andExpect(status().isBadRequest());

        List<Expense> expenseList = expenseRepository.findAll();
        assertThat(expenseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllExpenses() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList
        restExpenseMockMvc.perform(get("/api/expenses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(expense.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())))
            .andExpect(jsonPath("$.[*].cost").value(hasItem(DEFAULT_COST.doubleValue())))
            .andExpect(jsonPath("$.[*].originalCost").value(hasItem(DEFAULT_ORIGINAL_COST.doubleValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].currencyType").value(hasItem(DEFAULT_CURRENCY_TYPE.toString())))
            .andExpect(jsonPath("$.[*].ticketContentType").value(hasItem(DEFAULT_TICKET_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].ticket").value(hasItem(Base64Utils.encodeToString(DEFAULT_TICKET))));
    }
    
    @Test
    @Transactional
    public void getExpense() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get the expense
        restExpenseMockMvc.perform(get("/api/expenses/{id}", expense.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(expense.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE.toString()))
            .andExpect(jsonPath("$.cost").value(DEFAULT_COST.doubleValue()))
            .andExpect(jsonPath("$.originalCost").value(DEFAULT_ORIGINAL_COST.doubleValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.currencyType").value(DEFAULT_CURRENCY_TYPE.toString()))
            .andExpect(jsonPath("$.ticketContentType").value(DEFAULT_TICKET_CONTENT_TYPE))
            .andExpect(jsonPath("$.ticket").value(Base64Utils.encodeToString(DEFAULT_TICKET)));
    }


    @Test
    @Transactional
    public void getExpensesByIdFiltering() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        Long id = expense.getId();

        defaultExpenseShouldBeFound("id.equals=" + id);
        defaultExpenseShouldNotBeFound("id.notEquals=" + id);

        defaultExpenseShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultExpenseShouldNotBeFound("id.greaterThan=" + id);

        defaultExpenseShouldBeFound("id.lessThanOrEqual=" + id);
        defaultExpenseShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllExpensesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where name equals to DEFAULT_NAME
        defaultExpenseShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the expenseList where name equals to UPDATED_NAME
        defaultExpenseShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllExpensesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where name not equals to DEFAULT_NAME
        defaultExpenseShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the expenseList where name not equals to UPDATED_NAME
        defaultExpenseShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllExpensesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where name in DEFAULT_NAME or UPDATED_NAME
        defaultExpenseShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the expenseList where name equals to UPDATED_NAME
        defaultExpenseShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllExpensesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where name is not null
        defaultExpenseShouldBeFound("name.specified=true");

        // Get all the expenseList where name is null
        defaultExpenseShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllExpensesByNameContainsSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where name contains DEFAULT_NAME
        defaultExpenseShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the expenseList where name contains UPDATED_NAME
        defaultExpenseShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllExpensesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where name does not contain DEFAULT_NAME
        defaultExpenseShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the expenseList where name does not contain UPDATED_NAME
        defaultExpenseShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllExpensesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where description equals to DEFAULT_DESCRIPTION
        defaultExpenseShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the expenseList where description equals to UPDATED_DESCRIPTION
        defaultExpenseShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllExpensesByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where description not equals to DEFAULT_DESCRIPTION
        defaultExpenseShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the expenseList where description not equals to UPDATED_DESCRIPTION
        defaultExpenseShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllExpensesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultExpenseShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the expenseList where description equals to UPDATED_DESCRIPTION
        defaultExpenseShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllExpensesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where description is not null
        defaultExpenseShouldBeFound("description.specified=true");

        // Get all the expenseList where description is null
        defaultExpenseShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllExpensesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where description contains DEFAULT_DESCRIPTION
        defaultExpenseShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the expenseList where description contains UPDATED_DESCRIPTION
        defaultExpenseShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllExpensesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where description does not contain DEFAULT_DESCRIPTION
        defaultExpenseShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the expenseList where description does not contain UPDATED_DESCRIPTION
        defaultExpenseShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllExpensesByStateIsEqualToSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where state equals to DEFAULT_STATE
        defaultExpenseShouldBeFound("state.equals=" + DEFAULT_STATE);

        // Get all the expenseList where state equals to UPDATED_STATE
        defaultExpenseShouldNotBeFound("state.equals=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    public void getAllExpensesByStateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where state not equals to DEFAULT_STATE
        defaultExpenseShouldNotBeFound("state.notEquals=" + DEFAULT_STATE);

        // Get all the expenseList where state not equals to UPDATED_STATE
        defaultExpenseShouldBeFound("state.notEquals=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    public void getAllExpensesByStateIsInShouldWork() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where state in DEFAULT_STATE or UPDATED_STATE
        defaultExpenseShouldBeFound("state.in=" + DEFAULT_STATE + "," + UPDATED_STATE);

        // Get all the expenseList where state equals to UPDATED_STATE
        defaultExpenseShouldNotBeFound("state.in=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    public void getAllExpensesByStateIsNullOrNotNull() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where state is not null
        defaultExpenseShouldBeFound("state.specified=true");

        // Get all the expenseList where state is null
        defaultExpenseShouldNotBeFound("state.specified=false");
    }

    @Test
    @Transactional
    public void getAllExpensesByCostIsEqualToSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where cost equals to DEFAULT_COST
        defaultExpenseShouldBeFound("cost.equals=" + DEFAULT_COST);

        // Get all the expenseList where cost equals to UPDATED_COST
        defaultExpenseShouldNotBeFound("cost.equals=" + UPDATED_COST);
    }

    @Test
    @Transactional
    public void getAllExpensesByCostIsNotEqualToSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where cost not equals to DEFAULT_COST
        defaultExpenseShouldNotBeFound("cost.notEquals=" + DEFAULT_COST);

        // Get all the expenseList where cost not equals to UPDATED_COST
        defaultExpenseShouldBeFound("cost.notEquals=" + UPDATED_COST);
    }

    @Test
    @Transactional
    public void getAllExpensesByCostIsInShouldWork() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where cost in DEFAULT_COST or UPDATED_COST
        defaultExpenseShouldBeFound("cost.in=" + DEFAULT_COST + "," + UPDATED_COST);

        // Get all the expenseList where cost equals to UPDATED_COST
        defaultExpenseShouldNotBeFound("cost.in=" + UPDATED_COST);
    }

    @Test
    @Transactional
    public void getAllExpensesByCostIsNullOrNotNull() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where cost is not null
        defaultExpenseShouldBeFound("cost.specified=true");

        // Get all the expenseList where cost is null
        defaultExpenseShouldNotBeFound("cost.specified=false");
    }

    @Test
    @Transactional
    public void getAllExpensesByCostIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where cost is greater than or equal to DEFAULT_COST
        defaultExpenseShouldBeFound("cost.greaterThanOrEqual=" + DEFAULT_COST);

        // Get all the expenseList where cost is greater than or equal to UPDATED_COST
        defaultExpenseShouldNotBeFound("cost.greaterThanOrEqual=" + UPDATED_COST);
    }

    @Test
    @Transactional
    public void getAllExpensesByCostIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where cost is less than or equal to DEFAULT_COST
        defaultExpenseShouldBeFound("cost.lessThanOrEqual=" + DEFAULT_COST);

        // Get all the expenseList where cost is less than or equal to SMALLER_COST
        defaultExpenseShouldNotBeFound("cost.lessThanOrEqual=" + SMALLER_COST);
    }

    @Test
    @Transactional
    public void getAllExpensesByCostIsLessThanSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where cost is less than DEFAULT_COST
        defaultExpenseShouldNotBeFound("cost.lessThan=" + DEFAULT_COST);

        // Get all the expenseList where cost is less than UPDATED_COST
        defaultExpenseShouldBeFound("cost.lessThan=" + UPDATED_COST);
    }

    @Test
    @Transactional
    public void getAllExpensesByCostIsGreaterThanSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where cost is greater than DEFAULT_COST
        defaultExpenseShouldNotBeFound("cost.greaterThan=" + DEFAULT_COST);

        // Get all the expenseList where cost is greater than SMALLER_COST
        defaultExpenseShouldBeFound("cost.greaterThan=" + SMALLER_COST);
    }


    @Test
    @Transactional
    public void getAllExpensesByOriginalCostIsEqualToSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where originalCost equals to DEFAULT_ORIGINAL_COST
        defaultExpenseShouldBeFound("originalCost.equals=" + DEFAULT_ORIGINAL_COST);

        // Get all the expenseList where originalCost equals to UPDATED_ORIGINAL_COST
        defaultExpenseShouldNotBeFound("originalCost.equals=" + UPDATED_ORIGINAL_COST);
    }

    @Test
    @Transactional
    public void getAllExpensesByOriginalCostIsNotEqualToSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where originalCost not equals to DEFAULT_ORIGINAL_COST
        defaultExpenseShouldNotBeFound("originalCost.notEquals=" + DEFAULT_ORIGINAL_COST);

        // Get all the expenseList where originalCost not equals to UPDATED_ORIGINAL_COST
        defaultExpenseShouldBeFound("originalCost.notEquals=" + UPDATED_ORIGINAL_COST);
    }

    @Test
    @Transactional
    public void getAllExpensesByOriginalCostIsInShouldWork() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where originalCost in DEFAULT_ORIGINAL_COST or UPDATED_ORIGINAL_COST
        defaultExpenseShouldBeFound("originalCost.in=" + DEFAULT_ORIGINAL_COST + "," + UPDATED_ORIGINAL_COST);

        // Get all the expenseList where originalCost equals to UPDATED_ORIGINAL_COST
        defaultExpenseShouldNotBeFound("originalCost.in=" + UPDATED_ORIGINAL_COST);
    }

    @Test
    @Transactional
    public void getAllExpensesByOriginalCostIsNullOrNotNull() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where originalCost is not null
        defaultExpenseShouldBeFound("originalCost.specified=true");

        // Get all the expenseList where originalCost is null
        defaultExpenseShouldNotBeFound("originalCost.specified=false");
    }

    @Test
    @Transactional
    public void getAllExpensesByOriginalCostIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where originalCost is greater than or equal to DEFAULT_ORIGINAL_COST
        defaultExpenseShouldBeFound("originalCost.greaterThanOrEqual=" + DEFAULT_ORIGINAL_COST);

        // Get all the expenseList where originalCost is greater than or equal to UPDATED_ORIGINAL_COST
        defaultExpenseShouldNotBeFound("originalCost.greaterThanOrEqual=" + UPDATED_ORIGINAL_COST);
    }

    @Test
    @Transactional
    public void getAllExpensesByOriginalCostIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where originalCost is less than or equal to DEFAULT_ORIGINAL_COST
        defaultExpenseShouldBeFound("originalCost.lessThanOrEqual=" + DEFAULT_ORIGINAL_COST);

        // Get all the expenseList where originalCost is less than or equal to SMALLER_ORIGINAL_COST
        defaultExpenseShouldNotBeFound("originalCost.lessThanOrEqual=" + SMALLER_ORIGINAL_COST);
    }

    @Test
    @Transactional
    public void getAllExpensesByOriginalCostIsLessThanSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where originalCost is less than DEFAULT_ORIGINAL_COST
        defaultExpenseShouldNotBeFound("originalCost.lessThan=" + DEFAULT_ORIGINAL_COST);

        // Get all the expenseList where originalCost is less than UPDATED_ORIGINAL_COST
        defaultExpenseShouldBeFound("originalCost.lessThan=" + UPDATED_ORIGINAL_COST);
    }

    @Test
    @Transactional
    public void getAllExpensesByOriginalCostIsGreaterThanSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where originalCost is greater than DEFAULT_ORIGINAL_COST
        defaultExpenseShouldNotBeFound("originalCost.greaterThan=" + DEFAULT_ORIGINAL_COST);

        // Get all the expenseList where originalCost is greater than SMALLER_ORIGINAL_COST
        defaultExpenseShouldBeFound("originalCost.greaterThan=" + SMALLER_ORIGINAL_COST);
    }


    @Test
    @Transactional
    public void getAllExpensesByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where date equals to DEFAULT_DATE
        defaultExpenseShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the expenseList where date equals to UPDATED_DATE
        defaultExpenseShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllExpensesByDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where date not equals to DEFAULT_DATE
        defaultExpenseShouldNotBeFound("date.notEquals=" + DEFAULT_DATE);

        // Get all the expenseList where date not equals to UPDATED_DATE
        defaultExpenseShouldBeFound("date.notEquals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllExpensesByDateIsInShouldWork() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where date in DEFAULT_DATE or UPDATED_DATE
        defaultExpenseShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the expenseList where date equals to UPDATED_DATE
        defaultExpenseShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllExpensesByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where date is not null
        defaultExpenseShouldBeFound("date.specified=true");

        // Get all the expenseList where date is null
        defaultExpenseShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    public void getAllExpensesByCurrencyTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where currencyType equals to DEFAULT_CURRENCY_TYPE
        defaultExpenseShouldBeFound("currencyType.equals=" + DEFAULT_CURRENCY_TYPE);

        // Get all the expenseList where currencyType equals to UPDATED_CURRENCY_TYPE
        defaultExpenseShouldNotBeFound("currencyType.equals=" + UPDATED_CURRENCY_TYPE);
    }

    @Test
    @Transactional
    public void getAllExpensesByCurrencyTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where currencyType not equals to DEFAULT_CURRENCY_TYPE
        defaultExpenseShouldNotBeFound("currencyType.notEquals=" + DEFAULT_CURRENCY_TYPE);

        // Get all the expenseList where currencyType not equals to UPDATED_CURRENCY_TYPE
        defaultExpenseShouldBeFound("currencyType.notEquals=" + UPDATED_CURRENCY_TYPE);
    }

    @Test
    @Transactional
    public void getAllExpensesByCurrencyTypeIsInShouldWork() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where currencyType in DEFAULT_CURRENCY_TYPE or UPDATED_CURRENCY_TYPE
        defaultExpenseShouldBeFound("currencyType.in=" + DEFAULT_CURRENCY_TYPE + "," + UPDATED_CURRENCY_TYPE);

        // Get all the expenseList where currencyType equals to UPDATED_CURRENCY_TYPE
        defaultExpenseShouldNotBeFound("currencyType.in=" + UPDATED_CURRENCY_TYPE);
    }

    @Test
    @Transactional
    public void getAllExpensesByCurrencyTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenseList where currencyType is not null
        defaultExpenseShouldBeFound("currencyType.specified=true");

        // Get all the expenseList where currencyType is null
        defaultExpenseShouldNotBeFound("currencyType.specified=false");
    }

    @Test
    @Transactional
    public void getAllExpensesByEventIsEqualToSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);
        Event event = EventResourceIT.createEntity(em);
        em.persist(event);
        em.flush();
        expense.setEvent(event);
        expenseRepository.saveAndFlush(expense);
        Long eventId = event.getId();

        // Get all the expenseList where event equals to eventId
        defaultExpenseShouldBeFound("eventId.equals=" + eventId);

        // Get all the expenseList where event equals to eventId + 1
        defaultExpenseShouldNotBeFound("eventId.equals=" + (eventId + 1));
    }


    @Test
    @Transactional
    public void getAllExpensesByParticipantIsEqualToSomething() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);
        Participant participant = ParticipantResourceIT.createEntity(em);
        em.persist(participant);
        em.flush();
        expense.setParticipant(participant);
        expenseRepository.saveAndFlush(expense);
        Long participantId = participant.getId();

        // Get all the expenseList where participant equals to participantId
        defaultExpenseShouldBeFound("participantId.equals=" + participantId);

        // Get all the expenseList where participant equals to participantId + 1
        defaultExpenseShouldNotBeFound("participantId.equals=" + (participantId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultExpenseShouldBeFound(String filter) throws Exception {
        restExpenseMockMvc.perform(get("/api/expenses?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(expense.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())))
            .andExpect(jsonPath("$.[*].cost").value(hasItem(DEFAULT_COST.doubleValue())))
            .andExpect(jsonPath("$.[*].originalCost").value(hasItem(DEFAULT_ORIGINAL_COST.doubleValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].currencyType").value(hasItem(DEFAULT_CURRENCY_TYPE.toString())))
            .andExpect(jsonPath("$.[*].ticketContentType").value(hasItem(DEFAULT_TICKET_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].ticket").value(hasItem(Base64Utils.encodeToString(DEFAULT_TICKET))));

        // Check, that the count call also returns 1
        restExpenseMockMvc.perform(get("/api/expenses/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultExpenseShouldNotBeFound(String filter) throws Exception {
        restExpenseMockMvc.perform(get("/api/expenses?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restExpenseMockMvc.perform(get("/api/expenses/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingExpense() throws Exception {
        // Get the expense
        restExpenseMockMvc.perform(get("/api/expenses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateExpense() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        int databaseSizeBeforeUpdate = expenseRepository.findAll().size();

        // Update the expense
        Expense updatedExpense = expenseRepository.findById(expense.getId()).get();
        // Disconnect from session so that the updates on updatedExpense are not directly saved in db
        em.detach(updatedExpense);
        updatedExpense
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .state(UPDATED_STATE)
            .cost(UPDATED_COST)
            .originalCost(UPDATED_ORIGINAL_COST)
            .date(UPDATED_DATE)
            .currencyType(UPDATED_CURRENCY_TYPE)
            .ticket(UPDATED_TICKET)
            .ticketContentType(UPDATED_TICKET_CONTENT_TYPE);
        ExpenseDTO expenseDTO = expenseMapper.toDto(updatedExpense);

        restExpenseMockMvc.perform(put("/api/expenses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(expenseDTO)))
            .andExpect(status().isOk());

        // Validate the Expense in the database
        List<Expense> expenseList = expenseRepository.findAll();
        assertThat(expenseList).hasSize(databaseSizeBeforeUpdate);
        Expense testExpense = expenseList.get(expenseList.size() - 1);
        assertThat(testExpense.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testExpense.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testExpense.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testExpense.getCost()).isEqualTo(UPDATED_COST);
        assertThat(testExpense.getOriginalCost()).isEqualTo(UPDATED_ORIGINAL_COST);
        assertThat(testExpense.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testExpense.getCurrencyType()).isEqualTo(UPDATED_CURRENCY_TYPE);
        assertThat(testExpense.getTicket()).isEqualTo(UPDATED_TICKET);
        assertThat(testExpense.getTicketContentType()).isEqualTo(UPDATED_TICKET_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void updateNonExistingExpense() throws Exception {
        int databaseSizeBeforeUpdate = expenseRepository.findAll().size();

        // Create the Expense
        ExpenseDTO expenseDTO = expenseMapper.toDto(expense);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExpenseMockMvc.perform(put("/api/expenses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(expenseDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Expense in the database
        List<Expense> expenseList = expenseRepository.findAll();
        assertThat(expenseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteExpense() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        int databaseSizeBeforeDelete = expenseRepository.findAll().size();

        // Delete the expense
        restExpenseMockMvc.perform(delete("/api/expenses/{id}", expense.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Expense> expenseList = expenseRepository.findAll();
        assertThat(expenseList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
