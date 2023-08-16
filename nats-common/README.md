# Nats JetStream Integration

Nats-common provides Message Queue, Key Value Store and MQ based RPC interfaces.

## Installation

**On Mac OS via brew:**

    brew install nats-server

**Or via tar package:**

Pick your version from [Nats-Server Releases](https://github.com/nats-io/nats-server/releases/)

    curl -L https://github.com/nats-io/nats-server/releases/download/v2.7.4/nats-server-v2.7.4-darwin-amd64.tar.gz -o nats-server.tar.gz
    tar -xzf nats-server.tar.gz 
    cd ./nats-server

**On Linux:**

Pick your version from [Nats-Server Releases](https://github.com/nats-io/nats-server/releases/)

    curl -L https://github.com/nats-io/nats-server/releases/download/v2.7.4/nats-server-v2.7.4-linux-amd64.tar.gz -o nats-server.tar.gz
    tar -xzf nats-server.tar.gz 
    cd ./nats-server

## Server Configuration

Copy following configuration to `./server.conf`

```
listen: 127.0.0.1:4222
http_port: 8222

debug:   false
trace:   false
logtime: true
logfile_size_limit: 1GB
log_file: "./nats-server.log"

authorization: {
user: "jkqj_mq",
    # password plain text: cswjggljrmpypwfccarzpjxG-urepqldkhecvnzxzmngotaqs-bkwdvjgipruectqcowoqb6nj
    password: "$2a$11$WGm1tJqm5RrxcHESegfJtezrKzcb23NhlvZkutIQ9oOSiPPDZGMey"
}

jetstream {
    store_dir: "./"
    max_mem: 1G,
    max_file: 100G
}
```

## Startup Nats Server

    ./nats-server -c ./server.conf

## Checking

Execute:    

    telnet localhost 4222

will get the output likes follows:

    Escape character is '^]'.
    INFO {"server_id":"NDYG2OECZG2MWD4BCWWHMZYXWWOPSLUSN4RZJX2JW4RMKNQNJSBG2DP6","server_name":"NDYG2OECZG2MWD4BCWWHMZYXWWOPSLUSN4RZJX2JW4RMKNQNJSBG2DP6","version":"2.7.4","proto":1,"git_commit":"a86b84a","go":"go1.17.8","host":"0.0.0.0","port":4222,"headers":true,"max_payload":1048576,"jetstream":true,"client_id":43,"client_ip":"::1"}

Open Nats Monitoring [http://localhost:8222](http://localhost:8222)

## Subject Pattern

Nats supports flexible subject matching, plz check [Subject-Based Messaging](https://docs.nats.io/nats-concepts/subjects) for details.

## Office Documentation

[Installation](https://docs.nats.io/running-a-nats-service/introduction)

[Nats Server Overview](https://docs.nats.io/)