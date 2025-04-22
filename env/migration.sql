-- Add description column to invoices table
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS description VARCHAR(1000);