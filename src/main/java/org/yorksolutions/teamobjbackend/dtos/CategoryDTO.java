package org.yorksolutions.teamobjbackend.dtos;

import java.util.List;

@SuppressWarnings("unused")
public class CategoryDTO extends RequestDTO
{
    public List<String> productIDs;
    public String categoryName;
}
