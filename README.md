# Hospital Notification Service

Microsservico responsavel por consumir eventos de agendamento publicados pelo `hospital-scheduling-service`.

## Responsabilidades

- Consumir eventos de criacao e atualizacao de agendamentos.
- Confirmar mensagens com ACK manual somente apos o processamento.
- Reenfileirar falhas transitorias para retry.
- Evitar processamento duplicado usando `eventId`.
- Ignorar eventos antigos usando `updatedAt`.
- Persistir o estado processado no banco `hospital_notification_db`.

O servico nao cria nem altera agendamentos e nao envia e-mail, SMS ou push nesta etapa.

## Filas RabbitMQ

- Exchange: `hospital.appointments`
- `hospital.appointments.created`
- `hospital.appointments.updated`
- Routing keys: `appointment.created` e `appointment.updated`

O payload atual inclui `eventId`, `eventType`, `appointmentId` e os dados do agendamento. Mensagens antigas sem esses metadados sao aceitas com um identificador deterministico de compatibilidade.

## Configuracao local

Dependencias esperadas:

- PostgreSQL em `localhost:5432`
- RabbitMQ em `localhost:5672`
- Consul em `localhost:8500`

O banco `hospital_notification_db` e criado pelo `hospital-infrastructure/init-databases.sql` em instalacoes novas. Em uma infraestrutura ja iniciada, crie o banco uma vez:

```bash
docker exec hospital-postgres psql -U admin -d postgres -c "CREATE DATABASE hospital_notification_db"
```

## Executar

O projeto possui Maven Wrapper proprio:

```bash
./mvnw spring-boot:run
```

Por padrao, a aplicacao usa a porta `8082`. O health check esta disponivel em:

```text
http://localhost:8082/actuator/health
```

Para compilar sem executar testes:

```bash
./mvnw -DskipTests clean compile
```
# hospital-history-service
