package org.yorksolutions.teamobjbackend;

import org.springframework.web.server.ResponseStatusException;

public interface NoArgResponseOperator
{
    public void op() throws ResponseStatusException;
}
