package cqrs.microservice.shared.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotFoundExceptionResponse {
    private String timestamp;
    private int status;
}
