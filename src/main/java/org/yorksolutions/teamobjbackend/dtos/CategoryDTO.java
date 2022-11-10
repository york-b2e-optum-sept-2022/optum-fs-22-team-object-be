package org.yorksolutions.teamobjbackend.dtos;

import java.util.List;

/**
 * A DTO representing adding or removing categories to products
 */
@SuppressWarnings("unused")
public class CategoryDTO extends RequestDTO
{
    /**
     * List of productIDs to be affected
     */
    public List<String> productIDs;
    /**
     * Name of category
     */
    public String categoryName;
}
