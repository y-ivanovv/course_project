# Отчёт о покрытии тестами (JaCoCo)

Отчёт сформирован автоматически (`mvn test`, плагин JaCoCo). Полный HTML-отчёт —
в папке [jacoco-report/](jacoco-report/index.html) (открыть `index.html` в браузере).

## Итоговое покрытие

| Метрика | Значение |
|---------|----------|
| Покрытие инструкций (overall) | **45.7%** |
| Покрытие строк (overall) | **47.9%** |
| Тестов всего | 32 |

Порог методички (> 40%) выполнен.

## Покрытие по пакетам

| Пакет | Инструкции | Строки |
|-------|-----------:|-------:|
| ru.edu.project.control.advice (обработка ошибок) | 100.0% | 100.0% |
| ru.edu.project.foundation.security (перехватчик) | 100.0% | 100.0% |
| ru.edu.project.mediator.exceptions | 100.0% | 100.0% |
| ru.edu.project.mediator.services (бизнес-логика) | 79.3% | 81.7% |
| ru.edu.project.entity (доменные сущности) | 59.2% | 66.2% |
| ru.edu.project.control.dto | 47.4% | 48.8% |
| ru.edu.project.control.controllers | 0.0% | 0.0% |
| ru.edu.project.foundation.config | 0.0% | 0.0% |

## Комментарий

Тестами покрыта вся ключевая бизнес-логика (сервисы книг, выдач и пользователей),
обработчик ошибок и перехватчик авторизации. Контроллеры и конфигурация не покрыты
модульными тестами, так как требуют поднятия веб-контекста и инфраструктуры
(PostgreSQL + Elasticsearch); их проверка отнесена к интеграционному тестированию
(перспектива развития — тесты на Testcontainers).

## Как воспроизвести

```bash
cd library-backend
mvn test "-Dtest=BookServiceImplTest,UserServiceImplTest,LoanServiceImplTest,GlobalExceptionHandlerTest,AuthInterceptorTest,DtoTest"
# отчёт: target/site/jacoco/index.html
```
