package com.mariovn.eventmanager.service;

import java.util.List;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mariovn.eventmanager.domain.Event_;
import com.mariovn.eventmanager.domain.Participant;
// for static metamodels
import com.mariovn.eventmanager.domain.Participant_;
import com.mariovn.eventmanager.domain.UserExtra;
import com.mariovn.eventmanager.domain.UserExtra_;
import com.mariovn.eventmanager.domain.User_;
import com.mariovn.eventmanager.repository.UserExtraRepository;
import com.mariovn.eventmanager.service.dto.UserExtraCriteria;
import com.mariovn.eventmanager.service.dto.UserExtraDTO;
import com.mariovn.eventmanager.service.mapper.UserExtraMapper;

import io.github.jhipster.service.QueryService;
import io.github.jhipster.service.filter.LongFilter;

/**
 * Service for executing complex queries for {@link UserExtra} entities in the database.
 * The main input is a {@link UserExtraCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link UserExtraDTO} or a {@link Page} of {@link UserExtraDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UserExtraQueryService extends QueryService<UserExtra> {

    private final Logger log = LoggerFactory.getLogger(UserExtraQueryService.class);

    private final UserExtraRepository userExtraRepository;

    private final UserExtraMapper userExtraMapper;

    public UserExtraQueryService(UserExtraRepository userExtraRepository, UserExtraMapper userExtraMapper) {
        this.userExtraRepository = userExtraRepository;
        this.userExtraMapper = userExtraMapper;
    }

    /**
     * Return a {@link List} of {@link UserExtraDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<UserExtraDTO> findByCriteria(UserExtraCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<UserExtra> specification = createSpecification(criteria);
        return userExtraMapper.toDto(userExtraRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link UserExtraDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<UserExtraDTO> findByCriteria(UserExtraCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<UserExtra> specification = createSpecification(criteria);
        return userExtraRepository.findAll(specification, page)
            .map(userExtraMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UserExtraCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<UserExtra> specification = createSpecification(criteria);
        return userExtraRepository.count(specification);
    }

    /**
     * Function to convert {@link UserExtraCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<UserExtra> createSpecification(UserExtraCriteria criteria) {
        Specification<UserExtra> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), UserExtra_.id));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildSpecification(criteria.getUserId(),
                    root -> root.join(UserExtra_.user, JoinType.LEFT).get(User_.id)));
            }
            if (criteria.getParticipantId() != null) {
                specification = specification.and(buildSpecification(criteria.getParticipantId(),
                    root -> root.join(UserExtra_.participants, JoinType.LEFT).get(Participant_.id)));
            }
            if (criteria.getEventId() != null && criteria.getEventId().getEquals() != null) {            	
            	specification = Specification.where(notInEvent(criteria.getEventId().getEquals()));
            	
            }
        }
        return specification;
    }
    
    /**
     * Se crea subquery a partir del filtro
     * 
     * @param eventId id del evento
     * 
     * @return subconsulta que devuelve los usuarios del evento
     */
    private static Specification<UserExtra> notInEvent(Long eventId) {
    	return (root, query, builder) -> {
    		Subquery<UserExtra> subQuery = query.subquery(UserExtra.class);
    		Root<Participant> subRoot = subQuery.from(Participant.class);
    		
    		Join<Participant, UserExtra> subQueryParticipants = subRoot.join(Participant_.USER_EXTRA);
    		    		
    		subQuery.select(subQueryParticipants).where(builder.equal(subRoot.get(Participant_.EVENT), eventId));
    		
    		return builder.in(root).value(subQuery).not();
    	};
    }
}
