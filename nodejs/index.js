const {MongoClient} = require('mongodb')



async function listDatabases(client) {
    const databasesList = await client.db().admin().listDatabases();

    console.log("Databases: ");
    databasesList.databases.forEach(db => {
        console.log(`-${db.name}`);
    })
}


async function createListing(client, newListing) {
    const result = await client.db("M001").collection("realtime_trends").insertOne(newListing);
    console.log(`New listing created with the following id: ${result.insertedId}`);
}

async function createMultipleListings(client, newListings){
    const result = await client.db("M001").collection("realtime_trends").insertMany(newListings);
    console.log(`${result.insertedCount} new listing(s) created with the following id(s):`);
    console.log(result.insertedIds);       
}

async function findOneListingByName(client, nameOfListing) {
    const result = await client.db("M001").collection("sentiment_score").findOne(
        { 
            sentiment_type: nameOfListing 
        }
    );

    if (result) {
        console.log(`Found a listing in the collection with the name '${nameOfListing}':`);
        console.log(result);
    } else {
        console.log(`No listings found with the name '${nameOfListing}'`);
    }
}

async function findWithConditions(client) {
    const cursor = client.db("M001").collection("sentiment_score").find(
        {
            sentiment_type: "Positive",
            score: {$gt: 100}
        }).sort({score: -1}).limit(5)
    
    const result = await cursor.toArray();
    if (result.length > 0) {
        result.forEach(result => {
            console.log(`score: ${result.score}`)
            console.log(`sentiment_type: ${result.sentiment_type}`)
        })
    }
}

async function main() {
    /**
     * Connection URI. Update <username>, <password>, and <your-cluster-url> to reflect your cluster.
     * See https://docs.mongodb.com/ecosystem/drivers/node/ for more details
     */
    const uri = "mongodb+srv://nam130599:nam130599@cluster0.ebeqc.mongodb.net/?retryWrites=true&w=majority";
    
    const client = new MongoClient(uri);

    try {
        await client.connect();
        await listDatabases(client);

        await findOneListingByName(client, "Positive")

        await findWithConditions(client)
        // await createListing(client, {
        //     name: "Lovely Loft",
        //     summary: "A charming loft in Paris",
        //     bedrooms: 1,
        //     bathrooms: 1
        // });

        // await createMultipleListings(client, [
        //     {
        //         name: "Infinite Views",
        //         summary: "Modern home with infinite views from the infinity pool",
        //         property_type: "House",
        //         bedrooms: 5,
        //         bathrooms: 4.5,
        //         beds: 5
        //     },
        //     {
        //         name: "Private room in London",
        //         property_type: "Apartment",
        //         bedrooms: 1,
        //         bathroom: 1
        //     },
        //     {
        //         name: "Beautiful Beach House",
        //         summary: "Enjoy relaxed beach living in this house with a private beach",
        //         bedrooms: 4,
        //         bathrooms: 2.5,
        //         beds: 7,
        //         last_review: new Date()
        //     }
        // ]);

    } catch (err) {
        console.error(err)
    } finally {
        await client.close();
    }

}

main().catch(console.error)