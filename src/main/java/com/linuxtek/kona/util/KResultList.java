/*
 * Copyright (C) 2011 LinuxTek, Inc.  All Rights Reserved.
 */
package com.linuxtek.kona.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 */
@SuppressWarnings("serial")
public class KResultList<D> extends ArrayList<D> {

    private final static Integer MAX_RESULTS_PER_PAGE = 25;

    private Integer totalSize = null;
    private Integer startIndex = null;
    private Integer endIndex = null;
    //private Integer currentPage = null;
    private Integer maxResultsPerPage = MAX_RESULTS_PER_PAGE;

    public KResultList()
    {
        super();
    }
    
    public KResultList(Collection<D> c)
    {
        super(c);
    }

    public Integer getTotalSize() {
        return (totalSize);
    }

    public void setTotalSize(Integer totalSize) {
        this.totalSize = totalSize;
    }

    public Integer getStartIndex() {
        return (startIndex);
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getEndIndex() {
        return (endIndex);
    }

    public void setEndIndex(Integer endIndex) {
        this.endIndex = endIndex;
    }

    public Integer getMaxResultsPerPage() {
        return (maxResultsPerPage);
    }

    public void setMaxResultsPerPage(Integer maxResultsPerPage) {
        this.maxResultsPerPage = maxResultsPerPage;
    }

    // calculated value
    // NOTE: first page starts at 1 (not 0)
    public Integer getCurrentPage() {
        if (startIndex ==  null)
            return (null);

        Integer currentPage = (Integer) startIndex/maxResultsPerPage;
        return (currentPage + 1);
    }

    // calculated value
    public Integer getTotalPages() {
        if (totalSize == null)
            return (null);

        if (maxResultsPerPage == null)
            return (1);

        Integer totalPages = (Integer) totalSize/maxResultsPerPage;

        if ((totalSize % maxResultsPerPage) > 0)
            totalPages += 1;

        return (totalPages);
    }
}
