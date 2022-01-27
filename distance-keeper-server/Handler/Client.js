require('dotenv').config();


const URI = process.env.URI;

const MongoClient = require('mongodb').MongoClient;
let clientInstance = null;

class Client {
	
	constructor(){}
	
	static getInstance(){
		if (!clientInstance) return new Client();
		return clientInstance;
	}
	
	async clientOpen(){
		const clientConn = new Promise((resolve, reject) => {
			const mongoClient = new MongoClient(URI, { useNewUrlParser: true, useUnifiedTopology: true });
			mongoClient.connect(err => {
				if(err) resolve(null);
				resolve(mongoClient);
			});
		});
		return clientConn;
	}

	clientClose(){
		if (this.clientInstance && this.clientInstance.isConnected) this.clientInstance.close();
	}
}

module.exports = {
	Client
}