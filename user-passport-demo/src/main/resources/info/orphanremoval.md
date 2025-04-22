Ah, great observation! You're asking a deep and important question:

> 🔍 Even though `User` is **not the owning side** (because of `mappedBy = "user"`), can the `User` entity still control orphan removal via `orphanRemoval = true`?

### ✅ Short Answer:

**Yes**, the `User` entity *can* control `orphanRemoval`, **but only because it’s the *inverse side* of a bidirectional relationship and is responsible for managing the collection** (like `List<Address>`).

Let’s break this down carefully. 👇

---

## 🔄 Owning vs Inverse Side in JPA

In a bidirectional `@OneToMany` / `@ManyToOne`:

```java
// Parent (inverse side)
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Address> addresses;

// Child (owning side)
@ManyToOne
@JoinColumn(name = "user_id")
private User user;
```

- `Address.user` → **owning side**: because it holds the foreign key (`user_id`)
- `User.addresses` → **inverse side**: because it’s mappedBy

Despite this, **JPA allows you to control orphan removal from the inverse side**, because **you manage the collection**.

---

## 🧪 What Happens with Orphan Removal on the Inverse Side?

If you do this:

```java
user.getAddresses().remove(address); // Remove from list
userRepository.save(user);
```

And `orphanRemoval = true` is set on the `User.addresses` field, then:

☑️ JPA will:
- Remove the relationship **AND**
- Issue a `DELETE` query on the `Address` entity — **because it's now an orphan** (has no associated `User`).

---

## 💡 Why This Works

Even though `User` isn’t the owning side, it’s the one holding the *collection*. So JPA trusts you to manage the relationship correctly from the parent side — it watches the list.

In fact, **this only works because of how collections behave** in JPA:
- Removing from the collection = breaking the relationship.
- With `orphanRemoval = true`, that triggers delete.

---

## ⚠️ Gotchas

1. **Just setting `address.setUser(null)` is NOT enough** to delete the orphan — because that’s on the owning side. You need to also remove it from the `user.getAddresses()` collection for `orphanRemoval = true` to kick in.

2. **Consistency is key**: Always keep both sides of the relationship in sync when updating.

---

## ✅ TL;DR

| Concept | Answer |
|--------|--------|
| Is `User` the owning side? | ❌ No (`mappedBy = "user"`) |
| Can `User` still trigger orphan removal? | ✅ Yes, **via managing the collection** |
| What’s required? | Use `.remove(address)` on the list and ensure `orphanRemoval = true` is set |
| When is the child deleted? | When removed from collection **and** `orphanRemoval = true` |

---

Want a small runnable example to try it out and see the SQL behavior?