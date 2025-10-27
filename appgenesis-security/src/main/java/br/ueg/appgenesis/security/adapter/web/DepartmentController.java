package br.ueg.appgenesis.security.adapter.web;

import br.ueg.appgenesis.core.infrastructure.web.GenericDtoRestController;
import br.ueg.appgenesis.security.adapter.web.dto.DepartmentRequestDTO;
import br.ueg.appgenesis.security.adapter.web.dto.DepartmentResponseDTO;
import br.ueg.appgenesis.security.domain.Department;
import br.ueg.appgenesis.security.usecase.DepartmentService;
import br.ueg.appgenesis.security.adapter.mapper.DepartmentDtoMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/security/departments")
@Tag(name = "AppGenesis - Security - Departments")
public class DepartmentController extends GenericDtoRestController<Department, Long, DepartmentRequestDTO, DepartmentResponseDTO> {
    public DepartmentController(DepartmentService service, DepartmentDtoMapper mapper) {
        super(service, mapper);
    }
}
