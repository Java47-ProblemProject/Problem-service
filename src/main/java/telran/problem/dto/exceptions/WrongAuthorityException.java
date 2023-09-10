package telran.problem.dto.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class WrongAuthorityException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = -6073413693387062047L;
}
