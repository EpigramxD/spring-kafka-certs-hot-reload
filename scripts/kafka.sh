kafka-topics --bootstrap-server localhost:9092 --create --topic TEST
kafka-console-consumer --bootstrap-server localhost:9092 --topic TEST
kafka-console-producer --bootstrap-server localhost:9092 --topic TEST