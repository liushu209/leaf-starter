package com.jiangsilu.leaf;


import com.jiangsilu.leaf.exception.LeafException;

/**
 * @author sixuncle
 */
public interface IDGen {
    /**
     * 获得id
     *
     * @return long id
     * @throws LeafException
     */
    long get() throws LeafException;
}
