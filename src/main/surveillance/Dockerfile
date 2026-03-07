FROM golang:1.23-alpine AS builder
WORKDIR /app
COPY main.go .
RUN go mod init surveillance
RUN go build -o surveillance .

FROM alpine:latest
WORKDIR /root/
COPY --from=builder /app/surveillance .
EXPOSE 8080
CMD ["./surveillance"]