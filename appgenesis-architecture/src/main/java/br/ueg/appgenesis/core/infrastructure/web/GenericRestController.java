package br.ueg.appgenesis.core.infrastructure.web;

import br.ueg.appgenesis.core.port.GenericServicePort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

public abstract class GenericRestController<T, ID> {
    protected final GenericServicePort<T, ID> service;
    protected GenericRestController(GenericServicePort<T, ID> service) { this.service = service; }

    @PostMapping
    public ResponseEntity<T> create(@RequestBody T dto) { return ResponseEntity.ok(service.create(dto)); }

    @GetMapping
    public ResponseEntity<List<T>> findAll() { return ResponseEntity.ok(service.findAll()); }

    @GetMapping("/{id}")
    public ResponseEntity<T> find(@PathVariable("id") ID id) { return ResponseEntity.ok(service.find(id)); }

    @PutMapping("/{id}")
    public ResponseEntity<T> update(@PathVariable("id") ID id, @RequestBody T dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<T> patch(@PathVariable("id") ID id, @RequestBody T dto) {
        return ResponseEntity.ok(service.patch(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") ID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
