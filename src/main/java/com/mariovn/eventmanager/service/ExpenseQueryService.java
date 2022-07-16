package com.mariovn.eventmanager.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.mariovn.eventmanager.domain.Expense;
import com.mariovn.eventmanager.domain.*; // for static metamodels
import com.mariovn.eventmanager.repository.ExpenseRepository;
import com.mariovn.eventmanager.service.dto.ExpenseCriteria;
import com.mariovn.eventmanager.service.dto.ExpenseDTO;
import com.mariovn.eventmanager.service.mapper.ExpenseMapper;

/**
 * Service for executing complex queries for {@link Expense} entities in the database.
 * The main input is a {@link ExpenseCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ExpenseDTO} or a {@link Page} of {@link ExpenseDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ExpenseQueryService extends QueryService<Expense> {

    private final Logger log = LoggerFactory.getLogger(ExpenseQueryService.class);

    private final ExpenseRepository expenseRepository;

    private final ExpenseMapper expenseMapper;

    public ExpenseQueryService(ExpenseRepository expenseRepository, ExpenseMapper expenseMapper) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
    }

    /**
     * Return a {@link List} of {@link ExpenseDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ExpenseDTO> findByCriteria(ExpenseCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Expense> specification = createSpecification(criteria);
        return expenseMapper.toDto(expenseRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ExpenseDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ExpenseDTO> findByCriteria(ExpenseCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Expense> specification = createSpecification(criteria);
        return expenseRepository.findAll(specification, page)
            .map(expenseMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ExpenseCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Expense> specification = createSpecification(criteria);
        return expenseRepository.count(specification);
    }

    /**
     * Function to convert {@link ExpenseCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Expense> createSpecification(ExpenseCriteria criteria) {
        Specification<Expense> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Expense_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Expense_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Expense_.description));
            }
            if (criteria.getState() != null) {
                specification = specification.and(buildSpecification(criteria.getState(), Expense_.state));
            }
            if (criteria.getCost() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCost(), Expense_.cost));
            }
            if (criteria.getOriginalCost() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getOriginalCost(), Expense_.originalCost));
            }
            if (criteria.getDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDate(), Expense_.date));
            }
            if (criteria.getCurrencyType() != null) {
                specification = specification.and(buildSpecification(criteria.getCurrencyType(), Expense_.currencyType));
            }
            if (criteria.getEventId() != null) {
                specification = specification.and(buildSpecification(criteria.getEventId(),
                    root -> root.join(Expense_.event, JoinType.LEFT).get(Event_.id)));
            }
            if (criteria.getParticipantId() != null) {
                specification = specification.and(buildSpecification(criteria.getParticipantId(),
                    root -> root.join(Expense_.participant, JoinType.LEFT).get(Participant_.id)));
            }
        }
        return specification;
    }
}
