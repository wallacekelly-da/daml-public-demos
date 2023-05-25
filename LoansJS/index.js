import Ledger from "@daml/ledger";
import Loans from "@daml.js/loans"

const ledger = new Ledger.default({
  token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwczovL2RhbWwuY29tL2xlZGdlci1hcGkiOnsibGVkZ2VySWQiOiJzYW5kYm94IiwiYXBwbGljYXRpb25JZCI6ImZvb2JhciIsImFjdEFzIjpbIkxlbmRlcjo6MTIyMGRkNWFhZjI4YzA4ODk1ZTA0MTJkYTUwMTYyZDc2YTFlMDRlZTgwY2QyMmNmYTlhMWE3ZmY0MzVmODI3NDU2ZDQiXSwicmVhZEFzIjpbIkxlbmRlcjo6MTIyMGRkNWFhZjI4YzA4ODk1ZTA0MTJkYTUwMTYyZDc2YTFlMDRlZTgwY2QyMmNmYTlhMWE3ZmY0MzVmODI3NDU2ZDQiXX19.JG4uNPvOtrpjDomffHHHKHZ04cmtfVsgC8MxGCy-4xs",
  httpBaseUrl: "http://127.0.0.1:8080/"
});

const loans = await ledger.query(Loans.LoansV3.ILoan);

console.log("---------")
console.log("Loan count: " + loans.length);
loans.forEach(loan => {
  console.log("---")
  console.log("Created: " + loan.payload.created);
  console.log(" Amount: " + loan.payload.amount);
  console.log("   Rate: " + loan.payload.rate);
  console.log("  Basis: " + loan.payload.dayBasis);
});
