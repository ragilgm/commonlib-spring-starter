package com.commonlib.service.model;

import lombok.Data;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;

@Data
public class SearchCriteria<T> implements Serializable {

    private static final long serialVersionUID = 2405172041950251807L;

    private String key;
    private T value;
    private SearchOperation operation;

    public SearchCriteria(String key, T value, SearchOperation operation) {
        this.key = key;
        this.value = value;
        this.operation = operation;
    }

    public enum SearchOperation {
        GREATER_THAN {
            public void getOperation(List<Predicate> predicates, CriteriaBuilder builder, SearchCriteria<?> criteria, Root<?> root) {
                predicates.add(builder.greaterThan(
                        root.get(criteria.getKey()).as(String.class), criteria.getValue().toString()));
            }
        },
        LESS_THAN {
            public void getOperation(List<Predicate> predicates, CriteriaBuilder builder, SearchCriteria<?> criteria, Root<?> root) {
                predicates.add(builder.lessThan(
                        root.get(criteria.getKey()).as(String.class), criteria.getValue().toString()));
            }
        },
        GREATER_THAN_EQUAL {
            public void getOperation(List<Predicate> predicates, CriteriaBuilder builder, SearchCriteria<?> criteria, Root<?> root) {
                predicates.add(builder.greaterThanOrEqualTo(
                        root.get(criteria.getKey()).as(String.class), criteria.getValue().toString()));
            }
        },
        LESS_THAN_EQUAL {
            public void getOperation(List<Predicate> predicates, CriteriaBuilder builder, SearchCriteria<?> criteria, Root<?> root) {
                predicates.add(builder.lessThanOrEqualTo(
                        root.get(criteria.getKey()).as(String.class), criteria.getValue().toString()));
            }
        },
        NOT_EQUAL {
            public void getOperation(List<Predicate> predicates, CriteriaBuilder builder, SearchCriteria<?> criteria, Root<?> root) {
                predicates.add(builder.notEqual(
                        root.get(criteria.getKey()).as(String.class), criteria.getValue().toString()));
            }
        },
        EQUAL {
            public void getOperation(List<Predicate> predicates, CriteriaBuilder builder, SearchCriteria<?> criteria, Root<?> root) {
                predicates.add(builder.equal(
                        root.get(criteria.getKey()).as(String.class), criteria.getValue().toString()));
            }
        },
        MATCH {
            public void getOperation(List<Predicate> predicates, CriteriaBuilder builder, SearchCriteria<?> criteria, Root<?> root) {
                predicates.add(builder.like(
                        builder.lower(root.get(criteria.getKey()).as(String.class)),
                        "%" + criteria.getValue().toString().toLowerCase() + "%"));
            }
        },
        MATCH_END {
            public void getOperation(List<Predicate> predicates, CriteriaBuilder builder, SearchCriteria<?> criteria, Root<?> root) {
                predicates.add(builder.like(
                        builder.lower(root.get(criteria.getKey()).as(String.class)),
                        criteria.getValue().toString().toLowerCase() + "%"));
            }
        },
        EQUAL_IGNORE_CASE {
            public void getOperation(List<Predicate> predicates, CriteriaBuilder builder, SearchCriteria<?> criteria, Root<?> root) {
                predicates.add(builder.equal(
                        builder.lower(root.get(criteria.getKey()).as(String.class)), criteria.getValue().toString().toLowerCase()));
            }
        };

        public abstract void getOperation(List<Predicate> predicates, CriteriaBuilder builder, SearchCriteria<?> criteria, Root<?> root);
    }
}