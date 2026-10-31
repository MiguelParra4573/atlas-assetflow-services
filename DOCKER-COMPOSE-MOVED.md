# El docker-compose.yml se movio

La infraestructura local de Atlas AssetFlow vive ahora en `../platform/`, junto
con su `.env.example`, la configuracion de Prometheus y Grafana, y los scripts
de arranque.

```powershell
cd ..\platform
.\scripts\up.ps1 -Consoles
```

Los puertos cambiaron al bloque 152xx del proyecto, para que los cinco
proyectos del workspace puedan convivir: Mongo en 15205 (antes 27017) y RabbitMQ
en 15210 y 15211 (antes 5672 y 15672). La tabla completa esta en
`../platform/README.md`.
