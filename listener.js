const pg = require("pg")

// https://stackoverflow.com/a/49959557/167920
const keypress = async () => {
  process.stdin.setRawMode(true)
  return new Promise(resolve => process.stdin.once('data', () => {
    process.stdin.setRawMode(false)
    resolve()
  }))
}

async function main() {

  // https://node-postgres.com/apis/client#notification
  const client = new pg.Client({
    user: "postgres",
    password: "postgres",
    database: "postgres",
    port: 5432
  })

  client.on('notification', (msg) => { console.log(msg.payload) });
  client.on('error', _ => { console.log("There was an error.") });
  client.on('end', _ => {
    console.log("Shutting down.");
    process.exit(0);
  });

  await client.connect()
  await client.query('LISTEN pqs_watermark_changes');
  
  console.log("Listening...");
  console.log("Press any key to stop.\n");
  await keypress();
  await client.end();
}

main();