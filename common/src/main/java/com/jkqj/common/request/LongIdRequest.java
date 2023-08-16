package com.jkqj.common.request;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LongIdRequest implements Serializable {

    @NotNull(message = "id不能为空")
    private Long id;

}
