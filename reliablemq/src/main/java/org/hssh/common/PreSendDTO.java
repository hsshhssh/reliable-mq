package org.hssh.common;

import lombok.Data;

/**
 * Created by hssh on 2017/9/22.
 */
@Data
public class PreSendDTO
{
    private String queue;
    private String message;
    private String queryUrl;
}
