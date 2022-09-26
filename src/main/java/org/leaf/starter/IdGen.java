package org.leaf.starter;


import org.leaf.starter.exception.LeafException;

public interface IdGen {
    /**
     * 获得id
     *
     * @return long id
     * @throws LeafException
     */
    long get() throws LeafException;
}
