package com.mariovn.eventmanager.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;
import io.github.jhipster.service.filter.LongFilter;

import com.mariovn.eventmanager.domain.*; // for static metamodels
import com.mariovn.eventmanager.repository.ParticipantRepository;
import com.mariovn.eventmanager.repository.UserExtraRepository;
import com.mariovn.eventmanager.service.dto.ParticipantCriteria;
import com.mariovn.eventmanager.service.dto.ParticipantDTO;
import com.mariovn.eventmanager.service.mapper.ParticipantMapper;

/**
 * Service for executing complex queries for {@link Participant} entities in the database.
 * The main input is a {@link ParticipantCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ParticipantDTO} or a {@link Page} of {@link ParticipantDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ParticipantQueryService extends QueryService<Participant> {

    private final Logger log = LoggerFactory.getLogger(ParticipantQueryService.class);

    private final ParticipantRepository participantRepository;

    private final ParticipantMapper participantMapper;
    
    @Autowired
	private UserExtraRepository userExtraRepository;

    public ParticipantQueryService(ParticipantRepository participantRepository, ParticipantMapper participantMapper) {
        this.participantRepository = participantRepository;
        this.participantMapper = participantMapper;
    }

    /**
     * Return a {@link List} of {@link ParticipantDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ParticipantDTO> findByCriteria(ParticipantCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Participant> specification = createSpecification(criteria);
        return participantMapper.toDto(participantRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ParticipantDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ParticipantDTO> findByCriteria(ParticipantCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Participant> specification = createSpecification(criteria);
        return participantRepository.findAll(specification, page)
            .map(participantMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ParticipantCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Participant> specification = createSpecification(criteria);
        return participantRepository.count(specification);
    }
    
    @Transactional(readOnly = true)
    public Participant findByUserAndEvent(final User user, Long eventId) {
    	log.debug("find participant by user: " + user + " and eventId: " + eventId);
    	
    	Optional<UserExtra> userExtraOptional = userExtraRepository.findById(user.getId());
    	
    	if (!userExtraOptional.isPresent()) {
    		return null;
    	}
    	
    	UserExtra userExtra = userExtraOptional.get();
		
        ParticipantCriteria criteria = new ParticipantCriteria();
        LongFilter eventIdFilter = new LongFilter();
        LongFilter userExtraIdFilter = new LongFilter();
        
        eventIdFilter.setEquals(eventId);
        userExtraIdFilter.setEquals(userExtra.getId());
        criteria.setEventId(eventIdFilter);
        criteria.setUserExtraId(userExtraIdFilter);
        
        final Specification<Participant> specification = createSpecification(criteria);
        
        Optional<Participant> participantOptional = participantRepository.findOne(specification);
        
        Participant participant = null;
        if (participantOptional.isPresent()) {
        	participant = participantOptional.get();
        }
        
        return participant;
    }

    /**
     * Function to convert {@link ParticipantCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Participant> createSpecification(ParticipantCriteria criteria) {
        Specification<Participant> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Participant_.id));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildSpecification(criteria.getType(), Participant_.type));
            }
            if (criteria.getExpensesId() != null) {
                specification = specification.and(buildSpecification(criteria.getExpensesId(),
                    root -> root.join(Participant_.expenses, JoinType.LEFT).get(Expense_.id)));
            }
            if (criteria.getEventId() != null) {
                specification = specification.and(buildSpecification(criteria.getEventId(),
                    root -> root.join(Participant_.event, JoinType.LEFT).get(Event_.id)));
            }
            if (criteria.getUserExtraId() != null) {
                specification = specification.and(buildSpecification(criteria.getUserExtraId(),
                    root -> root.join(Participant_.userExtra, JoinType.LEFT).get(UserExtra_.id)));
            }
        }
        return specification;
    }
}
