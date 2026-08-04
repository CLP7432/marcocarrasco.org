package com.gasmanager.ventas.clients;

import com.gasmanager.ventas.dto.IncidenciaFaltanteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "microservice-nomina")
public interface NominaClient {

    @PostMapping("/api/incidencias")
    Object registrarIncidencia(@RequestBody IncidenciaFaltanteDTO incidencia);
}