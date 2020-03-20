package com.github.curriculeon.sorting;

import java.io.Serializable;

/**
 * @author leonhunter
 * @created 03/20/2020 - 9:37 AM
 */
public interface GenericBinaryInsertionSortInterface<
        SerializableType extends Serializable,
        ComparableType extends Comparable<SerializableType>> {
    ComparableType[] sort(ComparableType[] array);
}
