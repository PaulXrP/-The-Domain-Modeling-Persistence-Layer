Great timing to dive into **Fetch Type**! It plays a crucial role in **performance, memory usage, and query behavior**, especially in relationships like your **one-to-one between `User` and `Passport`**.

---

## 🔍 What is Fetch Type in JPA?

Fetch type determines **when** a related entity (like `Passport`) should be **fetched** from the database when its owning entity (`User`) is retrieved.

JPA gives you **two options**:

### ✅ `FetchType.EAGER` (default for `@OneToOne`)
- Fetches the associated entity **immediately** along with the owner.
- Happens via **`JOIN` or multiple queries**.

### 🚀 `FetchType.LAZY`
- Fetches the associated entity **only when accessed** (on demand).
- Requires **bytecode enhancement** or **proxying**.
- Hibernate might delay the fetch using **lazy loading mechanism**.

---

## 🧪 Your Case: Unidirectional One-to-One

```java
@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
@JoinColumn(name = "passport_id", referencedColumnName = "id")
private Passport passport;
```

Since you didn’t specify `fetch = ...`, JPA defaults to:

```java
@OneToOne(fetch = FetchType.EAGER, ...)
```

### What This Means:
When you do:

```java
User user = userRepository.findById(1L).get();
```

→ Hibernate will immediately load both:
- User row from `users` table
- Associated passport from `passport` table

### Result:
```sql
SELECT * FROM users WHERE id = 1;
SELECT * FROM passport WHERE id = (passport_id);
```
or it might do a single join:
```sql
SELECT u.*, p.* FROM users u
LEFT JOIN passport p ON u.passport_id = p.id
WHERE u.id = 1;
```

---

## 💡 When to Use `FetchType.LAZY` Instead?

If you **don’t always need the passport** when fetching users (e.g., in a list or lightweight DTO), use lazy loading to avoid unnecessary joins.

```java
@OneToOne(fetch = FetchType.LAZY, cascade = ...)
@JoinColumn(name = "passport_id")
private Passport passport;
```

This reduces initial query cost:
```sql
SELECT * FROM users WHERE id = 1;
-- Passport not fetched until getPassport() is accessed
```

Then only when:
```java
user.getPassport(); // triggers another SELECT
```

> **Important**: Lazy loading only works **within the same Hibernate session**. If you're outside (e.g., in a REST controller after session is closed), you'll get:
```
org.hibernate.LazyInitializationException
```

---

## 🔧 When Should You Use Each?

| Scenario | Fetch Type |
|----------|------------|
| You **always** need the associated entity (tight coupling) | `EAGER` |
| You **rarely** or **conditionally** need the association | `LAZY` |
| You're displaying a **list of Users**, and passport isn't needed | `LAZY` |
| You’re accessing entity from a **detached session** (e.g. in JSON/API) | `EAGER` (or preload manually) |

---

## 🚨 Tips & Pitfalls

1. **Avoid EAGER by default in large graphs**
    - It can trigger the dreaded **N+1 problem**
    - Example: 10 users → 10 separate passport queries

2. **Use DTO Projections** when returning large sets of data:
   ```java
   @Query("SELECT new com.dto.UserSummary(u.name, u.email) FROM User u")
   ```

3. **Use `JOIN FETCH`** when you want to load lazily-related data on demand:
   ```java
   @Query("SELECT u FROM User u JOIN FETCH u.passport WHERE u.id = :id")
   ```

---

## 🧠 Summary

| Feature | EAGER | LAZY |
|--------|-------|------|
| Loaded immediately? | ✅ Yes | ❌ No |
| Default for `@OneToOne` | ✅ | ❌ |
| Reduces code complexity | ✅ | ❌ (needs session awareness) |
| Efficient for small graphs | ✅ | ✅ |
| Best for optional/deep trees | ❌ | ✅ |

---

Would you like to see what the SQL logs look like for each fetch type in your example using Hibernate’s `show_sql` or `format_sql`?

Or do a live test with `LAZY` and catch the exception in a REST controller to see real behavior?