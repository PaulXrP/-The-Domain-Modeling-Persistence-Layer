Excellent line of thought, Pranay! Let's unpack it thoroughly.

---

## ✅ **Benefits of a Unidirectional One-to-One Relationship**

In a unidirectional `@OneToOne` relationship:

```java
@OneToOne
@JoinColumn(name = "passport_id")
private Passport passport;
```

Only the **owning side (User)** is aware of the relationship — the **other side (Passport)** is **not**.

---

### 💡 **Why and When to Use Unidirectional One-to-One**

| ✅ Use Case | 📌 Description |
|------------|----------------|
| **Simplicity** | If the other side (e.g., `Passport`) doesn't need to reference the `User`, this keeps your model clean and easier to maintain. |
| **Read-only association** | When you *always* navigate from `User` → `Passport`, not the other way around. |
| **Encapsulation** | If `Passport` is considered a private detail of `User` and shouldn’t be reused or linked from other entities. |
| **Avoiding extra joins** | Keeps the join logic and foreign key maintenance solely on the owner side, reducing potential inconsistencies. |
| **Write performance** | In some use cases, unidirectional relations may reduce the number of insert/update operations needed.

---

### 📍 Where Does the Foreign Key Live?

In your example:
```java
@OneToOne
@JoinColumn(name = "passport_id")
private Passport passport;
```

This means:
- The **`User` table owns** the relationship.
- The **foreign key column** `passport_id` is in the **`users`** table.
- So `User` → `Passport` (and not the other way).

> ✔️ **Yes, in JPA/Hibernate, the foreign key always lives in the table that owns the relationship.**

---

## 🔄 Comparison with Bidirectional One-to-One

| Aspect | Unidirectional | Bidirectional |
|--------|----------------|---------------|
| Mapping Simplicity | ✅ Simpler | ❌ More complex |
| Navigation | One direction only | Both directions |
| Database Join | Still possible | Still possible |
| Foreign Key Placement | Owner side | Still only one owner — FK is in owning side |
| Use Case | Private/detail relationship | Mutual ownership / cross-navigation |
| Memory Overhead | Lower | Slightly higher due to references on both sides |

---

## 🔧 When Would Bidirectional Be Better?

- If you **need to navigate from both ends**, e.g., `passport.getUser()` in business logic.
- If `Passport` needs to know about its owner for logic (e.g., auditing, validation).
- For **ORM cascades or bidirectional constraints** where both entities interact more closely.

---

## 🧠 Your Case

Your model:
```java
User (owns) → Passport
```

- Works great when `User` is the main access point.
- You're using **unidirectional** `@OneToOne` mapping correctly.
- The `passport_id` foreign key in `users` is **expected and efficient**.

---

Let me know if you'd like to explore how to make it **bidirectional** and when Hibernate actually creates a **shared primary key** (another flavor of 1:1 relationship!).




Great follow-up, Pranay 👏 Let’s dive deep into *how* only the owning side (i.e., `User`) is aware of the relationship in a **unidirectional `@OneToOne`** mapping.

---

## 🚦 Let's Look at Your Code

```java
// User.java (OWNING SIDE)
@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
@JoinColumn(name = "passport_id")
private Passport passport;
```

```java
// Passport.java (NO REFERENCE TO USER)
@Entity
public class Passport {
   // No reference back to User
}
```

---

## 🔍 So How Is `User` the Only Side Aware?

### ✅ In Java (Object Model):
- `User` has a reference to `Passport` via:
  ```java
  private Passport passport;
  ```
- `Passport` has **no reference back** to `User`.

This means in code:
```java
User user = ...;
Passport p = user.getPassport(); // ✅ Works

Passport passport = ...;
// passport.getUser(); ❌ Not possible – there's no such reference
```

---

### ✅ In JPA (Database Mapping):
```java
@OneToOne
@JoinColumn(name = "passport_id")
```
- This tells JPA: **Put the foreign key `passport_id` in the `users` table.**
- No mapping exists on the `Passport` side, so it’s not part of the entity graph from `Passport`.

The resulting DB schema:

| users (table)        |
|----------------------|
| id                   |
| name                 |
| email                |
| passport_id (FK)     |

| passport (table)     |
|----------------------|
| id                   |
| passport_number      |
| nationality          |

Thus:
- Hibernate knows only that `User → Passport`.
- `Passport` doesn’t know anything about `User`.

---

### 🔥 That’s Why It’s *Unidirectional*

- Direction of relationship is only from `User` → `Passport`
- No bidirectional navigation or ownership
- The entity graph looks like:

```
User
  |
  └───→ Passport
```

And not:

```
User ⇄ Passport
```

---

## ⚙️ Internally (Hibernate Session Context):
Hibernate builds a **persistence context** that includes relationships. Since `Passport` has no reverse mapping to `User`, Hibernate never considers `User` to be part of the graph **when working with `Passport` alone**.

So, for example:
```java
Passport p = entityManager.find(Passport.class, id);
// p.getUser(); ❌ will fail or doesn't exist at all
```

---

## 🔄 Summary

| Aspect | Unidirectional One-to-One |
|--------|----------------------------|
| Who owns FK? | `User` |
| Who has reference? | Only `User` |
| Who can navigate? | Only `User → Passport` |
| DB Join happens where? | FK lives in `users` table |
| Hibernate aware of both? | No — only `User` has the relationship info |

Would you like to **add bidirectionality** just to see how Hibernate behaves differently in both Java and SQL?

