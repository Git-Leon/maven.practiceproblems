package com.github.curriculeon.sorting;

import java.io.Serializable;

/**
 * @author leonhunter
 * @created 03/20/2020 - 9:41 AM
 */
public interface BinaryInsertionSortInterface<
        SerializableType extends Serializable>
        extends GenericBinaryInsertionSortInterface<SerializableType, Comparable<SerializableType>> {
}
