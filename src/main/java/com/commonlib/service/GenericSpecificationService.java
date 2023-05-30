package com.commonlib.service;

import com.commonlib.service.model.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class GenericSpecificationService<T> implements Specification<T> {

    private static final long serialVersionUID = 1900581010229669687L;

    private List<SearchCriteria<?>> searchCriteriaList;
    private Specification<T> specification;

    /**
     * GenericSpecificationService membuat factory baru
     */
    public GenericSpecificationService() {
        this.searchCriteriaList = new ArrayList<>();
    }

    public GenericSpecificationService(Specification<T> specification) {
        this.searchCriteriaList = new ArrayList<>();
        this.specification = specification;
    }

    /**
     * Digunakan untuk menambahkan kriteria pencarian
     *
     * @param criteria {@link SearchCriteria}
     */
    public void add(SearchCriteria<?> criteria) {
        if (!Objects.isNull(criteria.getValue())) {
            searchCriteriaList.add(criteria);
        }
    }

    /**
     * @param root {@link Root}
     * @param query {@link CriteriaQuery}
     * @param builder {@link CriteriaBuilder}
     * @return {@link Predicate}
     */
    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        //create a new predicate list
        List<Predicate> predicates = new ArrayList<>();

        if (this.specification != null) {
            predicates.add(this.specification.toPredicate(root, query, builder));
        }

        //add add criteria to predicates
        searchCriteriaList.forEach(criteria -> criteria.getOperation().getOperation(predicates, builder, criteria, root));

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
