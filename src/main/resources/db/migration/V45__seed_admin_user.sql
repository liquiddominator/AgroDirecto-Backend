INSERT INTO agro_directo.users (
    full_name,
    email,
    password_hash,
    phone,
    status_id,
    accepted_terms,
    accepted_privacy_policy,
    email_verified,
    phone_verified,
    failed_login_attempts,
    locked_until,
    last_login_at,
    password_changed_at
)
SELECT
    'Administrador AgroDirecto',
    'admin@agrodirecto.local',
    '$2a$10$i.wN2dp9bFhVJbiTwkVskuIl.EP9AEGvd9RBlUR0D5ba78vFT0PFe',
    '00000000',
    us.id,
    TRUE,
    TRUE,
    TRUE,
    TRUE,
    0,
    NULL,
    NULL,
    CURRENT_TIMESTAMP
FROM agro_directo.user_statuses us
WHERE us.code = 'VERIFIED'
ON CONFLICT (email) DO UPDATE SET
    full_name = EXCLUDED.full_name,
    password_hash = EXCLUDED.password_hash,
    phone = EXCLUDED.phone,
    status_id = EXCLUDED.status_id,
    accepted_terms = TRUE,
    accepted_privacy_policy = TRUE,
    email_verified = TRUE,
    phone_verified = TRUE,
    failed_login_attempts = 0,
    locked_until = NULL,
    password_changed_at = CURRENT_TIMESTAMP,
    updated_at = CURRENT_TIMESTAMP;

UPDATE agro_directo.user_roles
SET is_primary = FALSE
WHERE user_id = (
    SELECT id
    FROM agro_directo.users
    WHERE email = 'admin@agrodirecto.local'
);

INSERT INTO agro_directo.user_roles (
    user_id,
    role_id,
    is_primary
)
SELECT
    u.id,
    r.id,
    TRUE
FROM agro_directo.users u
JOIN agro_directo.roles r ON r.code = 'ADMIN'
WHERE u.email = 'admin@agrodirecto.local'
ON CONFLICT (user_id, role_id) DO UPDATE SET
    is_primary = TRUE;
