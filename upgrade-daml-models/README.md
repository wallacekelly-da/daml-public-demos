# Daml Public Demos by Wallace Kelly

Each demo is in its own Git branch.

## Demonstrate the challenge

1. Start with a Daml sandbox ledger, with Demo.1.0.0.dar installed,
which included a setup script for creating a Loan contract.

```
git rebase -i main

# editing each commit

git clean -xdf

cd Loans

daml sandbox

daml json-api --ledger-host localhost --ledger-port 6865 --http-port 8080

daml navigator server localhost 6865 --port 8081 --feature-user-management false

daml build 

daml ledger upload-dar --host localhost --port 6865 .daml/dist/Loans-1.0.0.dar

daml script --ledger-host localhost --ledger-port 6865 --dar .daml/dist/Loans-1.0.0.dar --script-name Main:setup --output-file parties.json

daml ledger list-parties
```

1. Use jwt.io to generate a token for a specific party:

```
{
  "alg": "HS256",
  "typ": "JWT"
}
```

```
{
  "https://daml.com/ledger-api": {
    "ledgerId": "sandbox",
    "applicationId": "Upgradability Demo",
    "actAs": [
      "Lender::12208...d3fdc"
    ],
    "readAs": [
      "Lender::12208...d3fdc"
    ]
  }
}
```

2. Use a node.js script to get the Loans, replacing the JWT key.

```
cd LoansJS

npm install

npm run codegen

npm start

git add .

git commit --amend
```

3. Create a Demo.2.0.0.dar with a new choice added to Loan,
and still a setup script to create a Loan contract.

```
git rebase --continue

daml build 

daml ledger upload-dar --host localhost --port 6865 .daml/dist/Loans-2.0.0.dar

daml script --ledger-host localhost --ledger-port 6865 --dar .daml/dist/Loans-2.0.0.dar --script-name Main:setup

npm start
```

Observe that the LoanJS project does not get the second loan.

4. Update the LoansJS project to use the Loans.2.0.0 dar.
 
```
git rebase --continue 

daml build

daml script --ledger-host localhost --ledger-port 6865 --dar .daml/dist/Loans-2.0.0.dar --script-name Main:setup

npm install

npm run codegen

npm start
```

5. Observe that the Node backend does not get the first loan.

## Demonstrate an upgrading setup script

1. Rename the Loan module to LoanV2.

```
daml build 

daml ledger upload-dar --host localhost --port 6865 .daml/dist/Loans-2.0.0.dar

daml script --ledger-host localhost --ledger-port 6865 --dar .daml/dist/Loans-2.0.0.dar --script-name Main:setup
```
 
2. Create an LoansV1V2 project, with references to both V1 and V2.

```
daml build 

daml ledger upload-dar --host localhost --port 6865 .daml/dist/LoansV1V2-0.0.1.dar

daml script --ledger-host localhost --ledger-port 6865 --dar .daml/dist/LoansV1V2-0.0.1.dar --script-name Main:setup
```

3. Re-run the node.js script to get the two Loans.

```
npm install

npm run codegen

npm start
```

## Demonstrate an interfaces based solution

1. Create a Demo.3.0.0, which makes the borrower an observer
2. Apply the "retrospective interfaces" technique.

```
daml build 

daml ledger upload-dar --host localhost --port 6865 .daml/dist/Loans-3.0.0.dar

daml script --ledger-host localhost --ledger-port 6865 --dar .daml/dist/Loans-3.0.0.dar --script-name Main:setup
```

3. Re-run the node.js script see the two Loans with the Decimal field.

```
npm install

npm run codegen

npm start
```
