package br.ueg.appgenesis.core.domain.error;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "ProblemDetail")
@Getter
@Setter
public class ProblemDetail {
    public String code;
    public String type;
    public String title;
    public Integer status;
    public String detail;
    public String instance;
    public String traceId;

    @ArraySchema(schema = @Schema(implementation = ProblemProperties.class))
    public java.util.List<ProblemProperties> properties;
}
