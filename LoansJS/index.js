import Ledger from "@daml/ledger";
import Loans from "@daml.js/loans"

const ledger = new Ledger.default({
  token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwczovL2RhbWwuY29tL2xlZGdlci1hcGkiOnsibGVkZ2VySWQiOiJzYW5kYm94IiwiYXBwbGljYXRpb25JZCI6ImZvb2JhciIsImFjdEFzIjpbIkxlbmRlcjo6MTIyMDFlMTYzODg5YzdlYzhhYThiMzVlMjdlZDhlMThjMTA3MWI5ZGU0MDQ3MzhhMGIwYWQ3OGNmZDVkMmY3ZGY4ZWQiXSwicmVhZEFzIjpbIkxlbmRlcjo6MTIyMDFlMTYzODg5YzdlYzhhYThiMzVlMjdlZDhlMThjMTA3MWI5ZGU0MDQ3MzhhMGIwYWQ3OGNmZDVkMmY3ZGY4ZWQiXX19.f9ifiieNWvr01Astdrr3u-ebpRVMjHUacU59E-cYuwQ",
  httpBaseUrl: "http://127.0.0.1:8080/"
});

const loans = await ledger.query(Loans.LoansV2.Loan);

console.log("---------")
console.log("Loan count: " + loans.length);
loans.forEach(loan => {
  console.log("---")
  console.log("Created: " + loan.payload.created);
  console.log(" Amount: " + loan.payload.amount);
  console.log("   Rate: " + loan.payload.rate);
});
