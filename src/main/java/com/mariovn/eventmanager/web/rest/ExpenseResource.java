package com.mariovn.eventmanager.web.rest;

import com.mariovn.eventmanager.service.ExpenseService;
import com.mariovn.eventmanager.service.ParticipantQueryService;
import com.mariovn.eventmanager.service.ParticipantService;
import com.mariovn.eventmanager.web.rest.errors.BadRequestAlertException;
import com.mariovn.eventmanager.service.dto.ExpenseDTO;
import com.mariovn.eventmanager.service.dto.ExpenseCriteria;
import com.mariovn.eventmanager.domain.Participant;
import com.mariovn.eventmanager.domain.User;
import com.mariovn.eventmanager.repository.UserRepository;
import com.mariovn.eventmanager.service.ExpenseQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.mariovn.eventmanager.domain.Expense}.
 */
@RestController
@RequestMapping("/api")
public class ExpenseResource {

    private final Logger log = LoggerFactory.getLogger(ExpenseResource.class);

    private static final String ENTITY_NAME = "expense";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExpenseService expenseService;

    private final ExpenseQueryService expenseQueryService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ParticipantQueryService participantQueryService;

    public ExpenseResource(ExpenseService expenseService, ExpenseQueryService expenseQueryService) {
        this.expenseService = expenseService;
        this.expenseQueryService = expenseQueryService;
    }

    /**
     * {@code POST  /expenses} : Create a new expense.
     *
     * @param expenseDTO the expenseDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new expenseDTO, or with status {@code 400 (Bad Request)} if the expense has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/expenses")
    public ResponseEntity<ExpenseDTO> createExpense(@Valid @RequestBody ExpenseDTO expenseDTO) throws URISyntaxException {
        log.debug("REST request to save Expense : {}", expenseDTO);
        if (expenseDTO.getId() != null) {
            throw new BadRequestAlertException("A new expense cannot already have an ID", ENTITY_NAME, "idexists");
        }
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Optional<User> user = userRepository.findOneByLogin(authentication.getName());
        
        Participant participant = participantQueryService.findByUserAndEvent(user.get(), expenseDTO.getEventId());
        
        expenseDTO.setParticipantId(participant.getId());
        
        ExpenseDTO result = expenseService.save(expenseDTO);
        return ResponseEntity.created(new URI("/api/expenses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /expenses} : Updates an existing expense.
     *
     * @param expenseDTO the expenseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated expenseDTO,
     * or with status {@code 400 (Bad Request)} if the expenseDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the expenseDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/expenses")
    public ResponseEntity<ExpenseDTO> updateExpense(@Valid @RequestBody ExpenseDTO expenseDTO) throws URISyntaxException {
        log.debug("REST request to update Expense : {}", expenseDTO);
        if (expenseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ExpenseDTO result = expenseService.save(expenseDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, expenseDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /expenses} : get all the expenses.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of expenses in body.
     */
    @GetMapping("/expenses")
    public ResponseEntity<List<ExpenseDTO>> getAllExpenses(ExpenseCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Expenses by criteria: {}", criteria);
        Page<ExpenseDTO> page = expenseQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /expenses/count} : count all the expenses.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/expenses/count")
    public ResponseEntity<Long> countExpenses(ExpenseCriteria criteria) {
        log.debug("REST request to count Expenses by criteria: {}", criteria);
        return ResponseEntity.ok().body(expenseQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /expenses/:id} : get the "id" expense.
     *
     * @param id the id of the expenseDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the expenseDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/expenses/{id}")
    public ResponseEntity<ExpenseDTO> getExpense(@PathVariable Long id) {
        log.debug("REST request to get Expense : {}", id);
        Optional<ExpenseDTO> expenseDTO = expenseService.findOne(id);
        return ResponseUtil.wrapOrNotFound(expenseDTO);
    }

    /**
     * {@code DELETE  /expenses/:id} : delete the "id" expense.
     *
     * @param id the id of the expenseDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        log.debug("REST request to delete Expense : {}", id);
        expenseService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
