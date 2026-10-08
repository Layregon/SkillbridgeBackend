package com.inserta.gestioncompetencias.carga;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.inserta.gestioncompetencias.modelo.*;
import com.inserta.gestioncompetencias.repositorio.*;

/**
 * Carga de datos de demostracion siguiendo el patron CommandLineRunner visto en clase.
 * Se puede activar o desactivar desde application.properties sin tocar el codigo Java.
 */
@Component
public class DatosIniciales implements CommandLineRunner {
    @Value("${app.datos-iniciales:true}")
    private boolean activado;

    private final PerfilRepository perfilRepo;
    private final SedeRepository sedeRepo;
    private final ProfesionalRepository profesionalRepo;
    private final CompetenciaRepository competenciaRepo;
    private final ProfesionalCompetenciaRepository pcRepo;
    private final CertificacionRepository certificacionRepo;
    private final ServicioRepository servicioRepo;
    private final AsignacionRepository asignacionRepo;
    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;

    public DatosIniciales(PerfilRepository perfilRepo, SedeRepository sedeRepo,
            ProfesionalRepository profesionalRepo, CompetenciaRepository competenciaRepo,
            ProfesionalCompetenciaRepository pcRepo, CertificacionRepository certificacionRepo,
            ServicioRepository servicioRepo, AsignacionRepository asignacionRepo,
            UsuarioRepository usuarioRepo, PasswordEncoder passwordEncoder) {
        this.perfilRepo = perfilRepo;
        this.sedeRepo = sedeRepo;
        this.profesionalRepo = profesionalRepo;
        this.competenciaRepo = competenciaRepo;
        this.pcRepo = pcRepo;
        this.certificacionRepo = certificacionRepo;
        this.servicioRepo = servicioRepo;
        this.asignacionRepo = asignacionRepo;
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!activado) {
            System.out.println("DatosIniciales DESACTIVADO. Usa database/gestion_competencias_workbench.sql si prefieres cargar la demo por SQL.");
            return;
        }

        // Usuarios demo para entrar desde Angular.
        if (usuarioRepo.count() == 0) {
            usuarioRepo.save(new Usuario(null, "admin", passwordEncoder.encode("admin123"), "ADMIN", true));
            usuarioRepo.save(new Usuario(null, "usuario", passwordEncoder.encode("usuario123"), "USER", true));
        }

        // Si ya se ha ejecutado el SQL de Workbench, no duplicamos la informacion de negocio.
        if (profesionalRepo.count() > 0) return;

        // 1. Catalogos base. Se conservan los IDs/datos de la BBDD aportada.
        Perfil backend = perfilRepo.save(nuevoPerfil("Backend"));
        Perfil frontend = perfilRepo.save(nuevoPerfil("Frontend"));
        Perfil devops = perfilRepo.save(nuevoPerfil("DevOps"));
        Perfil qa = perfilRepo.save(nuevoPerfil("QA"));

        Sede sevilla = sedeRepo.save(nuevaSede("Sevilla", "España"));
        Sede madrid = sedeRepo.save(nuevaSede("Madrid", "España"));
        Sede malaga = sedeRepo.save(nuevaSede("Málaga", "España"));

        // 2. Profesionales.
        Profesional ana = profesionalRepo.save(nuevoProfesional("Ana Torres", "ana@demo.local", 5, backend, sevilla));
        Profesional carlos = profesionalRepo.save(nuevoProfesional("Carlos Ruiz", "carlos@demo.local", 3, frontend, sevilla));
        Profesional lucia = profesionalRepo.save(nuevoProfesional("Lucía Martín", "lucia@demo.local", 7, devops, madrid));
        Profesional javier = profesionalRepo.save(nuevoProfesional("Javier Mora", "javier@demo.local", 2, qa, madrid));
        Profesional elena = profesionalRepo.save(nuevoProfesional("Elena Vega", "elena@demo.local", 4, backend, malaga));
        Profesional david = profesionalRepo.save(nuevoProfesional("David León", "david@demo.local", 1, frontend, malaga));

        // 3. Competencias.
        Competencia java = competenciaRepo.save(nuevaCompetencia("Java", "Desarrollo", null));
        Competencia angular = competenciaRepo.save(nuevaCompetencia("Angular", "Desarrollo", null));
        Competencia mysql = competenciaRepo.save(nuevaCompetencia("MySQL", "Datos", null));
        Competencia docker = competenciaRepo.save(nuevaCompetencia("Docker", "DevOps", null));
        Competencia testing = competenciaRepo.save(nuevaCompetencia("Testing", "Calidad", null));
        Competencia python = competenciaRepo.save(nuevaCompetencia("Python", "Datos", null));
        Competencia kubernetes = competenciaRepo.save(nuevaCompetencia("Kubernetes", "DevOps", null));

        pcRepo.saveAll(List.of(
            pc(ana, java, 5, new BigDecimal("5.0"), true), pc(ana, mysql, 4, new BigDecimal("4.0"), true), pc(ana, python, 3, new BigDecimal("2.0"), true),
            pc(carlos, angular, 5, null, null), pc(carlos, testing, 3, null, null),
            pc(lucia, docker, 5, null, null), pc(lucia, python, 3, null, null), pc(lucia, kubernetes, 4, null, null),
            pc(javier, mysql, 3, null, null), pc(javier, testing, 5, null, null),
            pc(elena, java, 4, null, null), pc(elena, mysql, 5, null, null),
            pc(david, java, 2, null, null), pc(david, angular, 4, null, null)
        ));

        // 4. Servicios y asignaciones.
        Servicio portal = servicioRepo.save(nuevoServicio("Portal clientes", "Cliente Norte", LocalDate.of(2026, 7, 16), LocalDate.of(2027, 2, 16)));
        Servicio datos = servicioRepo.save(nuevoServicio("Modernización datos", "Cliente Centro", LocalDate.of(2026, 8, 16), LocalDate.of(2027, 1, 16)));
        Servicio soporte = servicioRepo.save(nuevoServicio("Soporte plataforma", "Cliente Sur", LocalDate.of(2026, 5, 16), LocalDate.of(2027, 5, 16)));

        asignacionRepo.saveAll(List.of(
            asig(ana, portal, 70, "Backend"),
            asig(carlos, portal, 100, "Frontend"),
            asig(lucia, soporte, 100, "DevOps"),
            asig(javier, portal, 50, "QA"),
            asig(elena, datos, 80, "Backend"),
            asig(elena, soporte, 40, "Soporte"),
            asig(david, datos, 40, "Frontend")
        ));

        // 5. Certificaciones mostradas en los mockups/BBDD de referencia.
        certificacionRepo.saveAll(List.of(
            cert(ana, "Java SE", "Oracle", LocalDate.of(2026, 10, 31)),
            cert(elena, "MySQL Developer", null, LocalDate.of(2026, 10, 11))
        ));

        System.out.println("Datos demo cargados: 6 profesionales, 7 competencias, 3 servicios y 7 asignaciones");
    }


    private Perfil nuevoPerfil(String nombre) {
        Perfil p = new Perfil();
        p.setNombre(nombre);
        return p;
    }

    private Sede nuevaSede(String ciudad, String pais) {
        Sede s = new Sede();
        s.setCiudad(ciudad);
        s.setPais(pais);
        return s;
    }

    private Profesional nuevoProfesional(String nombre, String email, int antiguedad, Perfil perfil, Sede sede) {
        Profesional p = new Profesional();
        p.setNombre(nombre);
        p.setEmail(email);
        p.setAntiguedad(antiguedad);
        p.setActivo(true);
        p.setPerfil(perfil);
        p.setSede(sede);
        return p;
    }

    private Competencia nuevaCompetencia(String nombre, String area, String descripcion) {
        Competencia c = new Competencia();
        c.setNombre(nombre);
        c.setArea(area);
        c.setDescripcion(descripcion);
        return c;
    }

    private ProfesionalCompetencia pc(Profesional p, Competencia c, int nivel, BigDecimal anos, Boolean verificada) {
        ProfesionalCompetencia pc = new ProfesionalCompetencia();
        pc.setProfesional(p);
        pc.setCompetencia(c);
        pc.setNivel(nivel);
        pc.setAnosExperiencia(anos);
        pc.setVerificada(verificada);
        return pc;
    }

    private Servicio nuevoServicio(String nombre, String cliente, LocalDate inicio, LocalDate fin) {
        Servicio s = new Servicio();
        s.setNombre(nombre);
        s.setCliente(cliente);
        s.setFechaInicio(inicio);
        s.setFechaFin(fin);
        s.setActivo(true);
        return s;
    }

    private Asignacion asig(Profesional p, Servicio s, int porcentaje, String rol) {
        Asignacion a = new Asignacion();
        a.setProfesional(p);
        a.setServicio(s);
        a.setPorcentajeDedicacion(porcentaje);
        a.setRol(rol);
        a.setActiva(true);
        a.setFechaInicio(LocalDate.of(2026, 8, 16));
        return a;
    }

    private Certificacion cert(Profesional p, String nombre, String entidad, LocalDate caducidad) {
        Certificacion c = new Certificacion();
        c.setProfesional(p);
        c.setNombre(nombre);
        c.setEntidad(entidad);
        c.setFechaCaducidad(caducidad);
        return c;
    }
}
