# GasManager - Deploy con Docker Compose + Cloudflare Tunnel

## Servidor: Ubuntu 26.04 LTS (192.168.1.102)
## Fecha: 25 de Julio 2026

---

## Estado Actual
- Docker + Docker Compose instalados
- 15 contenedores corriendo (MySQL, Eureka, Gateway, 12 microservicios, Frontend)
- Frontend en puerto 80
- Quick Tunnel de Cloudflare funcionando (temporal)

## Puertos de microservicios
| Servicio | Puerto |
|---|---|
| Frontend | :80 |
| Gateway | :8085 |
| Eureka | :8761 |
| MySQL | :3307 |
| Users | :8081 |
| Ventas | :8082 |
| Clientes | :8093 |
| Facturacion | :8094 |
| Nomina | :8095 |
| Compras | :8096 |
| IA | :8097 |
| IoT | :8098 |
| Reportes | :8099 |
| Lealtad | :8100 |
| Inventarios | :8092 |

---

## Paso a Paso: Desplegar con Docker Compose

```bash
cd ~/proyectos/GasManager
docker compose up -d --build
```

Verificar:
```bash
docker compose ps
docker compose logs -f
```

---

## Paso a Paso: Conectar dominio marcocarrasco.org con Cloudflare

### Requisitos previos
1. Tener cuenta de Cloudflare (gratis)
2. Dominio marcocarrasco.org registrado
3. Agregar dominio en Cloudflare dashboard -> obtener nameservers
4. Cambiar nameservers en el registrador del dominio

### Ejecutar en el servidor
```bash
bash ~/setup-cloudflare-tunnel.sh
```

El script hace:
1. `cloudflared tunnel login` - abre navegador para autenticar
2. `cloudflared tunnel create gasmanager` - crea el tunnel
3. Configura `~/.cloudflared/config.yml`
4. `cloudflared tunnel route dns gasmanager marcocarrasco.org`
5. `cloudflared tunnel run gasmanager`

URL resultante: https://marcocarrasco.org

---

## SSH - Activar en el servidor

```bash
sudo apt install -y openssh-server
sudo systemctl enable ssh
sudo systemctl start ssh
```

Desde Windows:
```bash
ssh clp7432@192.168.1.102
```

---

## Comandos utiles

```bash
# Parar todo
docker compose down

# Parar y borrar volumenes
docker compose down -v

# Reconstruir un servicio
docker compose up -d --build msvc-ventas

# Ver recursos
docker stats

# Verificar tunnel
sudo systemctl status cloudflared

# Reiniciar tunnel
sudo systemctl restart cloudflared
```
