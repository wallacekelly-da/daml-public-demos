# Daml Public Demos by Wallace Kelly

When calling `lookupByKey`, it’s not enough to be a stakeholder of the contract. You must be the key maintainer.

## Example

Suppose you have this `Asset` template:

```
template Asset
  with
    issuer : Party
    owner  : Party
    name   : Text
  where
    signatory issuer
    observer owner
    key (issuer, name) : (Party, Text)
    maintainer key._1
```

And suppose you create a contract:

```
setup : Script ()
setup = script do
  issuer <- allocateParty "issuer"
  owner <- allocateParty "owner"

  tv <- submit issuer do
    createCmd Asset with name = "TV", ..
```

And then the `owner` tries to look up "his" contract by key:

```
template AssetLookup
  with
    looker : Party
  where
    signatory looker
    choice Lookup : Optional (ContractId Asset)
      with assetKey : (Party, Text) 
      controller looker
      do
        lookupByKey @Asset assetKey     
```

```
  submit owner do
    createAndExerciseCmd
      (AssetLookup with looker = owner)
      (Lookup with assetKey = (issuer, "TV"))
```

The runtime error is:

```
lookup by key of Main:Asset...
     failed due to a missing authorization from 'issuer'
```

## Getting the required authorization

You could modify the `AssetLookup` to include the authorization of the `issuer`:

```
template AssetLookup2
  with
    issuer : Party
    looker : Party
  where
    signatory issuer
    observer looker
    nonconsuming choice Lookup2 : Optional (ContractId Asset)
      with name : Text
      controller looker
      do
        lookupByKey @Asset (issuer, name)
```

And then the owner could lookup the contract by key, now that he has the authorization of issuer:

```
  lookup <- submit issuer do
    createCmd AssetLookup2 with looker = owner, ..
  
  Some tv2 <- submit owner do
    exerciseCmd lookup Lookup2 with name = "TV"
```

## Using fetchByKey instead

As discussed [in the forums](https://discuss.daml.com/t/lookupbykey-vs-fetchbykey-why-can-i-fetch-a-key-but-not-do-a-lookup/181/3?u=wallacekelly) and [in the docs](https://docs.daml.com/daml/reference/contract-keys.html), the authorization rules are different for `lookupByKey` and `fetchByKey`. Here is an example using `fetchByKey`:

```
template AssetFetch
  with fetcher : Party
  where
    signatory fetcher
    choice Fetch : (ContractId Asset, Asset)
      with assetKey : (Party, Text)
      controller fetcher
      do
        fetchByKey @Asset assetKey
```

```
  (tv3, _) <- submit owner do
    createAndExerciseCmd
      (AssetFetch with fetcher = owner)
      (Fetch with assetKey = (issuer, "TV"))
```

Which succeeds at runtime (assuming a contract with the given key exists.)