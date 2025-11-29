-- WARNING: This schema is for context only and is not meant to be run.
-- Table order and constraints may not be valid for execution.

CREATE TABLE public.administrador (
                                      persona_id bigint NOT NULL,
                                      CONSTRAINT administrador_pkey PRIMARY KEY (persona_id),
                                      CONSTRAINT administrador_persona_id_fkey FOREIGN KEY (persona_id) REFERENCES public.persona(id)
);
CREATE TABLE public.correlativa (
                                    materia_codigo character varying NOT NULL,
                                    materia_plan_codigo character varying NOT NULL,
                                    correlativa_codigo character varying NOT NULL,
                                    correlativa_plan_codigo character varying NOT NULL,
                                    CONSTRAINT correlativa_pkey PRIMARY KEY (correlativa_codigo, correlativa_plan_codigo, materia_codigo, materia_plan_codigo),
                                    CONSTRAINT correlativa_materia_codigo_materia_plan_codigo_fkey FOREIGN KEY (materia_codigo) REFERENCES public.materia(codigo),
                                    CONSTRAINT correlativa_materia_codigo_materia_plan_codigo_fkey FOREIGN KEY (materia_plan_codigo) REFERENCES public.materia(codigo),
                                    CONSTRAINT correlativa_materia_codigo_materia_plan_codigo_fkey FOREIGN KEY (materia_codigo) REFERENCES public.materia(plan_de_estudio_codigo),
                                    CONSTRAINT correlativa_materia_codigo_materia_plan_codigo_fkey FOREIGN KEY (materia_plan_codigo) REFERENCES public.materia(plan_de_estudio_codigo),
                                    CONSTRAINT correlativa_correlativa_codigo_correlativa_plan_codigo_fkey FOREIGN KEY (correlativa_codigo) REFERENCES public.materia(codigo),
                                    CONSTRAINT correlativa_correlativa_codigo_correlativa_plan_codigo_fkey FOREIGN KEY (correlativa_plan_codigo) REFERENCES public.materia(codigo),
                                    CONSTRAINT correlativa_correlativa_codigo_correlativa_plan_codigo_fkey FOREIGN KEY (correlativa_codigo) REFERENCES public.materia(plan_de_estudio_codigo),
                                    CONSTRAINT correlativa_correlativa_codigo_correlativa_plan_codigo_fkey FOREIGN KEY (correlativa_plan_codigo) REFERENCES public.materia(plan_de_estudio_codigo)
);
CREATE TABLE public.estadisticas_generales (
                                               id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
                                               cantidad_materia_mas_rendida bigint NOT NULL,
                                               distribucion_estudiantes_por_carrera text NOT NULL,
                                               distribucion_examenes_por_materia text NOT NULL,
                                               estudiantes_activos bigint NOT NULL,
                                               ultima_actualizacion timestamp without time zone NOT NULL,
                                               materia_mas_rendida text NOT NULL,
                                               porcentaje_aprobados_general double precision NOT NULL,
                                               promedio_general double precision NOT NULL,
                                               promedio_notas_por_materia text NOT NULL,
                                               top5aprobadas text NOT NULL,
                                               top5reprobadas text NOT NULL,
                                               total_examenes_rendidos integer NOT NULL,
                                               total_materias integer NOT NULL,
                                               CONSTRAINT estadisticas_generales_pkey PRIMARY KEY (id)
);
CREATE TABLE public.estadisticas_materia (
                                             codigo_materia character varying NOT NULL,
                                             periodo character varying NOT NULL,
                                             aprobados integer,
                                             distribucion_dificultad text,
                                             distribucion_modalidad text,
                                             distribucion_recursos text,
                                             ultima_actualizacion timestamp without time zone,
                                             nombre_materia character varying,
                                             promedio_dias_estudio double precision,
                                             promedio_dificultad double precision,
                                             promedio_horas_diarias double precision,
                                             promedio_notas double precision,
                                             reprobados integer,
                                             total_rendidos integer,
                                             CONSTRAINT estadisticas_materia_pkey PRIMARY KEY (codigo_materia, periodo)
);
CREATE TABLE public.estadisticas_por_carrera (
                                                 id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
                                                 cantidad_materia_mas_rendida bigint NOT NULL,
                                                 codigo_plan character varying NOT NULL,
                                                 distribucion_examenes_por_materia text NOT NULL,
                                                 estudiantes_activos integer NOT NULL,
                                                 fecha_ultima_actualizacion timestamp without time zone NOT NULL,
                                                 materia_mas_rendida text NOT NULL,
                                                 periodo character varying NOT NULL,
                                                 porcentaje_aprobados_general double precision NOT NULL,
                                                 promedio_general double precision NOT NULL,
                                                 promedio_notas_por_materia text NOT NULL,
                                                 top5aprobadas text NOT NULL,
                                                 top5reprobadas text NOT NULL,
                                                 total_examenes_rendidos integer NOT NULL,
                                                 total_materias integer NOT NULL,
                                                 CONSTRAINT estadisticas_por_carrera_pkey PRIMARY KEY (id)
);
CREATE TABLE public.estudiante (
                                   persona_id bigint NOT NULL,
                                   CONSTRAINT estudiante_pkey PRIMARY KEY (persona_id),
                                   CONSTRAINT estudiante_persona_id_fkey FOREIGN KEY (persona_id) REFERENCES public.persona(id)
);
CREATE TABLE public.examen (
                               id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
                               fecha date NOT NULL,
                               nota double precision,
                               renglon_id bigint UNIQUE,
                               CONSTRAINT examen_pkey PRIMARY KEY (id),
                               CONSTRAINT examen_renglon_id_fkey FOREIGN KEY (renglon_id) REFERENCES public.renglon(id)
);
CREATE TABLE public.experiencia (
                                    id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
                                    dias_estudio integer NOT NULL,
                                    dificultad integer NOT NULL,
                                    horas_diarias integer NOT NULL,
                                    intentos_previos integer NOT NULL,
                                    modalidad character varying,
                                    motivacion character varying,
                                    recursos character varying,
                                    examen_id bigint UNIQUE,
                                    link_resumen character varying,
                                    CONSTRAINT experiencia_pkey PRIMARY KEY (id),
                                    CONSTRAINT experiencia_examen_id_fkey FOREIGN KEY (examen_id) REFERENCES public.examen(id)
);
CREATE TABLE public.historia_academica (
                                           id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
                                           persona_id_estudiante bigint,
                                           plan_de_estudio_codigo character varying NOT NULL,
                                           estado character varying DEFAULT 'ACTIVA'::character varying,
                                           CONSTRAINT historia_academica_pkey PRIMARY KEY (id),
                                           CONSTRAINT historia_academica_persona_id_estudiante_fkey FOREIGN KEY (persona_id_estudiante) REFERENCES public.estudiante(persona_id),
                                           CONSTRAINT historia_academica_plan_de_estudio_codigo_fkey FOREIGN KEY (plan_de_estudio_codigo) REFERENCES public.plan_de_estudio(codigo)
);
CREATE TABLE public.materia (
                                codigo character varying NOT NULL,
                                nombre character varying NOT NULL,
                                plan_de_estudio_codigo character varying NOT NULL,
                                CONSTRAINT materia_pkey PRIMARY KEY (codigo, plan_de_estudio_codigo),
                                CONSTRAINT materia_plan_de_estudio_codigo_fkey FOREIGN KEY (plan_de_estudio_codigo) REFERENCES public.plan_de_estudio(codigo)
);
CREATE TABLE public.persona (
                                id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
                                mail character varying NOT NULL UNIQUE,
                                nombre_apellido character varying NOT NULL,
                                rol_usuario character varying NOT NULL,
                                supabase_user_id character varying NOT NULL UNIQUE,
                                telefono character varying,
                                CONSTRAINT persona_pkey PRIMARY KEY (id)
);
CREATE TABLE public.plan_de_estudio (
                                        codigo character varying NOT NULL,
                                        propuesta character varying NOT NULL,
                                        CONSTRAINT plan_de_estudio_pkey PRIMARY KEY (codigo)
);
CREATE TABLE public.registro_inscripcion (
                                             id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
                                             anio integer NOT NULL CHECK (anio >= 2012 AND anio <= 2028),
                                             turno character varying,
                                             persona_id_estudiante bigint NOT NULL,
                                             materia_codigo character varying NOT NULL,
                                             materia_plan_codigo character varying NOT NULL,
                                             CONSTRAINT registro_inscripcion_pkey PRIMARY KEY (id),
                                             CONSTRAINT registro_inscripcion_persona_id_estudiante_fkey FOREIGN KEY (persona_id_estudiante) REFERENCES public.estudiante(persona_id),
                                             CONSTRAINT registro_inscripcion_materia_codigo_materia_plan_codigo_fkey FOREIGN KEY (materia_codigo) REFERENCES public.materia(codigo),
                                             CONSTRAINT registro_inscripcion_materia_codigo_materia_plan_codigo_fkey FOREIGN KEY (materia_plan_codigo) REFERENCES public.materia(codigo),
                                             CONSTRAINT registro_inscripcion_materia_codigo_materia_plan_codigo_fkey FOREIGN KEY (materia_codigo) REFERENCES public.materia(plan_de_estudio_codigo),
                                             CONSTRAINT registro_inscripcion_materia_codigo_materia_plan_codigo_fkey FOREIGN KEY (materia_plan_codigo) REFERENCES public.materia(plan_de_estudio_codigo)
);
CREATE TABLE public.renglon (
                                id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
                                fecha date NOT NULL,
                                nota double precision,
                                resultado character varying NOT NULL,
                                tipo character varying NOT NULL,
                                historia_id bigint NOT NULL,
                                materia_codigo character varying NOT NULL,
                                materia_plan_codigo character varying NOT NULL,
                                CONSTRAINT renglon_pkey PRIMARY KEY (id),
                                CONSTRAINT renglon_historia_id_fkey FOREIGN KEY (historia_id) REFERENCES public.historia_academica(id),
                                CONSTRAINT renglon_materia_codigo_materia_plan_codigo_fkey FOREIGN KEY (materia_codigo) REFERENCES public.materia(codigo),
                                CONSTRAINT renglon_materia_codigo_materia_plan_codigo_fkey FOREIGN KEY (materia_plan_codigo) REFERENCES public.materia(codigo),
                                CONSTRAINT renglon_materia_codigo_materia_plan_codigo_fkey FOREIGN KEY (materia_codigo) REFERENCES public.materia(plan_de_estudio_codigo),
                                CONSTRAINT renglon_materia_codigo_materia_plan_codigo_fkey FOREIGN KEY (materia_plan_codigo) REFERENCES public.materia(plan_de_estudio_codigo)
);