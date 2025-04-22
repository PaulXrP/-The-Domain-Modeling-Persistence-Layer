Superb question, Pranay! You’re hitting one of the **most misunderstood yet critical** aspects of JPA/Hibernate relationship modeling.

Let’s break it down clearly:

---

## 🎯 First, the Core Principle

> **Even in a bidirectional relationship in JPA, there is *always* one and only one owning side.**

That’s **by design** — JPA/Hibernate **requires** one side to own the association, to avoid ambiguity in mapping and database synchronization.

---

## 🤝 So What Happens in Bidirectional Relationships?

### Example:

```java
@Entity
public class User {
    @OneToOne(mappedBy = "user")
    private Passport passport; // ❌ Not the owner
}

@Entity
public class Passport {
    @OneToOne
    @JoinColumn(name = "user_id") // ✅ The owner
    private User user;
}
```

Here:
- `Passport` owns the relationship because it has `@JoinColumn`.
- `User` is mapped **by** the `Passport` side.

So, `Passport.user` is the owning side.
Hibernate will **generate the FK in the passport table**, like:
```sql
ALTER TABLE passport ADD CONSTRAINT FK_user FOREIGN KEY (user_id) REFERENCES user(id);
```

Even though both classes reference each other, only one will manage the foreign key.

---

## ❌ Can Both Be Owners?

Nope. **JPA prohibits both sides being owners of the same relationship.**

If you try something like:
```java
// User.java
@OneToOne
@JoinColumn(name = "passport_id") // trying to own
private Passport passport;

// Passport.java
@OneToOne
@JoinColumn(name = "user_id") // also trying to own
private User user;
```

🔥 **You’ll get errors** like:
- `Repeated column in mapping`
- `Conflicting foreign keys`
- Hibernate not knowing which table to place the FK in

Because:
> Two foreign keys imply two separate relationships — not a single one-to-one association anymore.

---

## 🧠 Analogy: Think of It Like a Lease Agreement

Imagine two roommates in a flat:
- One person signs the lease (the owner of the contract).
- The other is a co-tenant (but not the legal signer).

Even if both live there and talk about it — **only one is legally accountable**.

---

## ✅ Why JPA Does This

Because:
- It simplifies SQL joins, cascades, updates
- Prevents redundant or circular foreign key constraints
- Clearly tells Hibernate **who is responsible for maintaining the DB FK and cascading actions**

---

## 🔄 What If You Want Foreign Keys on Both Sides?

You’ll need to:
- Model two separate relationships, not a single `@OneToOne`.
- Handle them manually (e.g., `@JoinTable` or separate join columns), but it’s *rare and discouraged*.

---

## 🧩 Summary

| Question | Answer |
|---------|--------|
| Can both sides be owners in a JPA `@OneToOne`? | ❌ No |
| Why? | To avoid ambiguity in DB mapping and FK handling |
| Who manages the FK? | Only the owning side (the one with `@JoinColumn`) |
| What happens if both try to own? | JPA errors and broken schema |

---

Let me know if you'd like to play around with both sides trying to own it and see how Hibernate reacts during schema generation 😄