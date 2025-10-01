#!/bin/bash

echo "🧪 Test della Configurazione CORS - Piattaforma Agricola"
echo "======================================================"

echo ""
echo "1. 📡 Test CORS OPTIONS Preflight"
echo "----------------------------------"

# Test OPTIONS preflight request
echo "Eseguo richiesta OPTIONS a http://localhost:8080/api/auth/register..."

response=$(curl -s -X OPTIONS \
  -H "Origin: http://localhost:4200" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  -w "HTTP_CODE:%{http_code}\nHEADERS:%{header_list}\n" \
  http://localhost:8080/api/auth/register)

http_code=$(echo "$response" | grep "HTTP_CODE:" | cut -d: -f2)
headers=$(echo "$response" | grep "HEADERS:" | cut -d: -f2-)

echo "Status Code: $http_code"

if [[ "$headers" == *"Access-Control-Allow-Origin"* ]]; then
    echo "✅ Headers CORS presenti"
else
    echo "❌ Headers CORS mancanti"
fi

echo ""
echo "2. 🔄 Test POST Registrazione (reale)"
echo "------------------------------------"

# Test POST request con dati di test
echo "Eseguo richiesta POST a http://localhost:8080/api/auth/register..."

post_response=$(curl -s -X POST \
  -H "Origin: http://localhost:4200" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser'$(date +%s)'",
    "email": "test'$(date +%s)'@example.com",
    "password": "TestPass123",
    "nome": "Test",
    "cognome": "User",
    "ruolo": "ACQUIRENTE",
    "telefono": "3331234567"
  }' \
  -w "HTTP_CODE:%{http_code}\nHEADERS:%{header_list}\n" \
  http://localhost:8080/api/auth/register)

post_http_code=$(echo "$post_response" | grep "HTTP_CODE:" | cut -d: -f2)
post_headers=$(echo "$post_response" | grep "HEADERS:" | cut -d: -f2-)

echo "Status Code: $post_http_code"

case $post_http_code in
    200|201)
        echo "✅ Registrazione riuscita!"
        ;;
    409)
        echo "✅ CORS OK, Utente già esistente"
        ;;
    403)
        echo "⚠️ CORS OK, Altro Errore (403)"
        ;;
    *)
        echo "❌ Errore imprevisto: $post_http_code"
        ;;
esac

if [[ "$post_headers" == *"Access-Control-Allow-Origin"* ]]; then
    echo "✅ Headers CORS presenti nella risposta POST"
else
    echo "❌ Headers CORS mancanti nella risposta POST"
fi

echo ""
echo "3. 📊 Risultato Finale"
echo "====================="

if [[ "$http_code" == "200" ]] && [[ "$post_headers" == *"Access-Control-Allow-Origin"* ]]; then
    echo "🎉 CORS CONFIGURATO CORRETTAMENTE!"
    echo "Il backend accetta richieste da http://localhost:4200"
    echo ""
    echo "✅ Prossimi passi:"
    echo "1. Riavvia il backend Spring Boot se necessario"
    echo "2. Avvia il frontend con 'npm start'"
    echo "3. Testa la registrazione dal browser"
else
    echo "❌ CORS NON CONFIGURATO CORRETTAMENTE"
    echo ""
    echo "🔧 Possibili soluzioni:"
    echo "1. Verifica che il backend sia in esecuzione"
    echo "2. Riavvia il backend Spring Boot"
    echo "3. Controlla i log del backend per errori"
    echo "4. Verifica la configurazione in WebConfig.java"
fi

echo ""
echo "4. 🌐 Test tramite Proxy Angular"
echo "==============================="

echo "Test della richiesta tramite proxy Angular (http://localhost:4200/api/auth/register)..."

proxy_response=$(curl -s -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testproxy'$(date +%s)'",
    "email": "testproxy'$(date +%s)'@example.com",
    "password": "TestPass123",
    "nome": "Test",
    "cognome": "Proxy",
    "ruolo": "ACQUIRENTE",
    "telefono": "3331234567"
  }' \
  -w "HTTP_CODE:%{http_code}\n" \
  http://localhost:4200/api/auth/register)

proxy_http_code=$(echo "$proxy_response" | grep "HTTP_CODE:" | cut -d: -f2)

echo "Status Code via Proxy: $proxy_http_code"

if [[ "$proxy_http_code" == "200" ]] || [[ "$proxy_http_code" == "201" ]] || [[ "$proxy_http_code" == "409" ]]; then
    echo "✅ Proxy Angular funzionante!"
else
    echo "❌ Proxy Angular non funzionante (codice: $proxy_http_code)"
    echo "Assicurati che il frontend sia in esecuzione con 'npm start'"
fi

echo ""
echo "Test completato!"