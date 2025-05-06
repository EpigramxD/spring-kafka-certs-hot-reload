openssl genrsa -out client1.key 2048
openssl req -new -key client1.key -out client1.csr -subj "/C=US/ST=State/L=City/O=MyOrg/OU=Kafka/CN=trustedClient"
openssl x509 -req -in client1.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out client1.crt -days 365 -sha256
mv client1.crt client1.pem



# http
# ca
openssl genrsa -aes256 -passout pass:password -out CA-spring.key 4096
openssl req -new -key CA-spring.key -passin pass:password -subj "/CN=test/" -out CA-spring-request.csr
openssl x509 -req -in CA-spring-request.csr -signkey CA-spring.key -passin pass:password -days 365 -out CA-spring.pem

# ca2
openssl genrsa -aes256 -passout pass:password -out CA-spring2.key 4096
openssl req -new -key CA-spring2.key -passin pass:password -subj "/CN=test2/" -out CA-spring-request2.csr
openssl x509 -req -in CA-spring-request2.csr -signkey CA-spring2.key -passin pass:password -days 365 -out CA-spring2.pem

# server
openssl genrsa -aes256 -passout pass:password -out server.key 4096
openssl req -new -key server.key -passin pass:password -subj "/CN=localhost/" -out server.csr
openssl x509 -req -in server.csr -CA ../CA-spring.pem -CAkey ../CA-spring.key -passin pass:password -CAcreateserial -days 365 -out server.pem

# server2
openssl genrsa -aes256 -passout pass:password -out server2.key 4096
openssl req -new -key server2.key -passin pass:password -subj "/CN=localhost/" -out server2.csr
openssl x509 -req -in server2.csr -CA ../CA/CA-spring.pem -CAkey ../CA/CA-spring.key -passin pass:password -CAcreateserial -days 365 -out server2.pem

# client
openssl genrsa -aes256 -passout pass:password -out client.key 4096
openssl req -new -key client.key -passin pass:password -subj "/CN=Client/" -out client.csr
openssl x509 -req -in client.csr -CA ../CA/CA-spring.pem -CAkey ../CA/CA-spring.key -passin pass:password -days 365 -out client.pem

# client2
openssl genrsa -aes256 -passout pass:password -out client2.key 4096
openssl req -new -key client2.key -passin pass:password -subj "/CN=Client/" -out client2.csr
openssl x509 -req -in client2.csr -CA ../CA/CA-spring2.pem -CAkey ../CA/CA-spring2.key -passin pass:password -days 365 -out client2.pem

