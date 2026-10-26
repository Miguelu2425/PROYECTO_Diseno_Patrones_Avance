-- ============================================================
-- Script T-SQL  |  Base de datos: Municipalidad
-- Proyecto      : Plataforma Integral de Atencion Ciudadana Municipal
-- Curso         : Diseno de Patrones - Semana 11
-- ============================================================

-- 1. CREAR LA BASE DE DATOS
IF NOT EXISTS (
    SELECT * FROM sys.databases WHERE name = 'Municipalidad'
)
BEGIN
    CREATE DATABASE Municipalidad;
    PRINT 'Base de datos Municipalidad creada correctamente.';
END
ELSE
BEGIN
    PRINT 'La base de datos Municipalidad ya existe.';
END
GO

USE Municipalidad;
GO

-- 2. TABLA: TipoTramite
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'TipoTramite')
BEGIN
    CREATE TABLE TipoTramite (
        id_tipo_tramite INT          IDENTITY(1,1) PRIMARY KEY,
        nombre          VARCHAR(50)  NOT NULL UNIQUE,
        descripcion     VARCHAR(200) NOT NULL
    );
    PRINT 'Tabla TipoTramite creada.';
END
GO

IF NOT EXISTS (SELECT 1 FROM TipoTramite WHERE nombre = 'Reclamo')
BEGIN
    INSERT INTO TipoTramite (nombre, descripcion) VALUES
        ('Reclamo',   'Queja o disconformidad vecinal sobre servicios municipales'),
        ('Solicitud', 'Peticion formal de servicio, licencia o tramite municipal');
    PRINT 'Tipos base insertados.';
END
GO

-- 3. TABLA: Ciudadano
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Ciudadano')
BEGIN
    CREATE TABLE Ciudadano (
        id_ciudadano INT          IDENTITY(1,1) PRIMARY KEY,
        dni          CHAR(8)      NOT NULL UNIQUE,
        nombre       VARCHAR(120) NOT NULL,
        correo       VARCHAR(120) NOT NULL
    );
    PRINT 'Tabla Ciudadano creada.';
END
GO

-- 4. TABLA: Expediente
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Expediente')
BEGIN
    CREATE TABLE Expediente (
        id_expediente   INT         IDENTITY(1,1) PRIMARY KEY,
        codigo          VARCHAR(20) NOT NULL UNIQUE,
        id_ciudadano    INT         NOT NULL
            CONSTRAINT FK_Expediente_Ciudadano
                REFERENCES Ciudadano(id_ciudadano),
        id_tipo_tramite INT         NOT NULL
            CONSTRAINT FK_Expediente_TipoTramite
                REFERENCES TipoTramite(id_tipo_tramite),
        estado          VARCHAR(30) NOT NULL DEFAULT 'En Revision',
        fecha_registro  DATETIME            DEFAULT GETDATE()
    );
    PRINT 'Tabla Expediente creada.';
END
GO

-- 5. PROCEDIMIENTO: sp_RegistrarTramite
IF OBJECT_ID('dbo.sp_RegistrarTramite', 'P') IS NOT NULL
    DROP PROCEDURE dbo.sp_RegistrarTramite;
GO

CREATE PROCEDURE dbo.sp_RegistrarTramite
    @dni          CHAR(8),
    @nombre       VARCHAR(120),
    @correo       VARCHAR(120),
    @tipo_tramite VARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @id_ciudadano    INT;
    DECLARE @id_tipo_tramite INT;
    DECLARE @codigo          VARCHAR(20);
    DECLARE @id_expediente   INT;

    BEGIN TRY
        BEGIN TRAN;

        -- Resolver o crear ciudadano
        SELECT @id_ciudadano = id_ciudadano FROM Ciudadano WHERE dni = @dni;
        IF @id_ciudadano IS NULL
        BEGIN
            INSERT INTO Ciudadano (dni, nombre, correo)
            VALUES (@dni, @nombre, @correo);
            SET @id_ciudadano = SCOPE_IDENTITY();
        END

        -- Resolver tipo de tramite
        SELECT @id_tipo_tramite = id_tipo_tramite FROM TipoTramite WHERE nombre = @tipo_tramite;
        IF @id_tipo_tramite IS NULL
            RAISERROR('Tipo de tramite no encontrado: %s', 16, 1, @tipo_tramite);

        -- Generar codigo unico: EXP-<aaaammdd>-<id_ciudadano>-<secuencia>
        DECLARE @sec INT;
        SELECT @sec = ISNULL(MAX(id_expediente), 0) + 1 FROM Expediente;
        SET @codigo = 'EXP-' + CONVERT(VARCHAR(8), GETDATE(), 112)
                    + '-' + CAST(@id_ciudadano AS VARCHAR(6))
                    + '-' + CAST(@sec AS VARCHAR(6));

        -- Insertar expediente
        INSERT INTO Expediente (codigo, id_ciudadano, id_tipo_tramite, estado)
        VALUES (@codigo, @id_ciudadano, @id_tipo_tramite, 'En Revision');
        SET @id_expediente = SCOPE_IDENTITY();

        COMMIT TRAN;

        -- Resultado
        SELECT e.id_expediente, e.codigo, c.dni, c.nombre AS ciudadano,
               t.nombre AS tipo_tramite, e.estado, e.fecha_registro
        FROM  Expediente  e
        JOIN  Ciudadano   c ON c.id_ciudadano    = e.id_ciudadano
        JOIN  TipoTramite t ON t.id_tipo_tramite = e.id_tipo_tramite
        WHERE e.id_expediente = @id_expediente;

    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRAN;
        DECLARE @msg   NVARCHAR(500) = ERROR_MESSAGE();
        DECLARE @sev   INT           = ERROR_SEVERITY();
        DECLARE @state INT           = ERROR_STATE();
        RAISERROR(@msg, @sev, @state);
    END CATCH
END
GO

PRINT 'Script ejecutado correctamente.';
GO
