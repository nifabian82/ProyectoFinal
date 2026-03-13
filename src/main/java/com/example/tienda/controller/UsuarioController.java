package com.example.tienda.controller;

import com.example.tienda.Service.QuejaService;
import com.example.tienda.Service.UsuarioService;
import com.example.tienda.DTO.UsuarioDTO;
import com.example.tienda.DTO.UsuarioCreatedDTO;
import com.example.tienda.DTO.QuejaDTO;
import com.example.tienda.Mapper.UsuarioMapper;
import com.example.tienda.model.usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {
    private final UsuarioService service;
    private final QuejaService quejaService;

    public UsuarioController(UsuarioService service, QuejaService quejaService) {
        this.service = service;
        this.quejaService = quejaService;
    }

    @Operation(summary = "Crear un nuevo usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public UsuarioDTO crearUsuario(@RequestBody UsuarioCreatedDTO dto) {
        usuario nuevo = new usuario();
        nuevo.setNombre(dto.getNombre());
        nuevo.setEmail(dto.getEmail());
        nuevo.setPassword(dto.getPassword());

        usuario guardado = service.guardar(nuevo);
        return UsuarioMapper.toDTO(guardado);
    }

    @Operation(summary = "Listar todos los usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de usuarios obtenido correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public List<UsuarioDTO> listarUsuarios() {
        return service.listarTodas()
                .stream()
                .map(UsuarioMapper::toDTO)
                .toList();
    }

    @Operation(summary = "Listar quejas de un usuario por ID")
    @GetMapping("/{id}/quejas")
    public List<QuejaDTO> listarQuejasDeUsuarios(@PathVariable Long id) {
        usuario user = service.obtenerPorId(id);
        return user.getQuejas()
                .stream()
                .map(UsuarioMapper::toQuejaDTO)
                .toList();
    }

    @Operation(summary = "Actualizar un usuario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")

    })
    @PutMapping("/{id}")
    public UsuarioDTO actualizarusuario(@PathVariable Long id, @RequestBody UsuarioCreatedDTO datos) {
        usuario actualizado = service.actualizar(id, datos);
        return UsuarioMapper.toDTO(actualizado);
    }

    @Operation(summary = "Eliminar un usuario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public void eliminarUsuario(@PathVariable Long id) {
        service.eliminar(id);
    }

    @Operation(summary = "Recuperar contraseña mediante respuesta de seguridad")
    @PostMapping("/recuperar")
    public String recuperarPassword(@RequestParam String email, @RequestParam String respuesta, @RequestParam String nuevaPassword) {
        boolean exito = service.recuperarPassword(email, respuesta, nuevaPassword);
        if (exito) {
            return "Contraseña actualizada correctamente.";
        } else {
            return "Error: Email o respuesta de seguridad incorrectos.";
        }
    }
}
