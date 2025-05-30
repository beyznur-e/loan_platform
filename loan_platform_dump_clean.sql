--
-- PostgreSQL database dump
--

-- Dumped from database version 15.11
-- Dumped by pg_dump version 15.11

-- Started on 2025-05-30 15:10:44

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
-- SELECT pg_catalog.set_config('search_path', '', false);
SET search_path TO public;
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

ALTER TABLE ONLY public.documents DROP CONSTRAINT "user";
ALTER TABLE ONLY public.batch_step_execution_context DROP CONSTRAINT step_exec_ctx_fk;
ALTER TABLE ONLY public.notifications DROP CONSTRAINT notifications_user_id_fkey;
ALTER TABLE ONLY public.loan_scoring DROP CONSTRAINT loan_scoring_application_id_fkey;
ALTER TABLE ONLY public.loan_payments DROP CONSTRAINT loan_payments_application_id_fkey;
ALTER TABLE ONLY public.loan_applications DROP CONSTRAINT loan_applications_user_id_fkey;
ALTER TABLE ONLY public.batch_job_execution DROP CONSTRAINT job_inst_exec_fk;
ALTER TABLE ONLY public.batch_step_execution DROP CONSTRAINT job_exec_step_fk;
ALTER TABLE ONLY public.batch_job_execution_params DROP CONSTRAINT job_exec_params_fk;
ALTER TABLE ONLY public.batch_job_execution_context DROP CONSTRAINT job_exec_ctx_fk;
ALTER TABLE ONLY public.bank_accounts DROP CONSTRAINT bank_accounts_user_id_fkey;
ALTER TABLE ONLY public.audit_logs DROP CONSTRAINT audit_logs_user_id_fkey;
ALTER TABLE ONLY public.documents DROP CONSTRAINT application;
ALTER TABLE ONLY public.users DROP CONSTRAINT users_pkey;
ALTER TABLE ONLY public.users DROP CONSTRAINT users_email_key;
ALTER TABLE ONLY public.loan_scoring DROP CONSTRAINT uq_loan_scoring_application;
ALTER TABLE ONLY public.notifications DROP CONSTRAINT notifications_pkey;
ALTER TABLE ONLY public.loan_scoring DROP CONSTRAINT loan_scoring_pkey;
ALTER TABLE ONLY public.loan_payments DROP CONSTRAINT loan_payments_pkey;
ALTER TABLE ONLY public.loan_applications DROP CONSTRAINT loan_applications_pkey;
ALTER TABLE ONLY public.batch_job_instance DROP CONSTRAINT job_inst_un;
ALTER TABLE ONLY public.documents DROP CONSTRAINT documents_pkey;
ALTER TABLE ONLY public.batch_step_execution DROP CONSTRAINT batch_step_execution_pkey;
ALTER TABLE ONLY public.batch_step_execution_context DROP CONSTRAINT batch_step_execution_context_pkey;
ALTER TABLE ONLY public.batch_job_instance DROP CONSTRAINT batch_job_instance_pkey;
ALTER TABLE ONLY public.batch_job_execution DROP CONSTRAINT batch_job_execution_pkey;
ALTER TABLE ONLY public.batch_job_execution_context DROP CONSTRAINT batch_job_execution_context_pkey;
ALTER TABLE ONLY public.bank_accounts DROP CONSTRAINT bank_accounts_pkey;
ALTER TABLE ONLY public.bank_accounts DROP CONSTRAINT bank_accounts_account_number_key;
ALTER TABLE ONLY public.audit_logs DROP CONSTRAINT audit_logs_pkey;
DROP TABLE public.users;
DROP TABLE public.notifications;
DROP TABLE public.loan_scoring;
DROP TABLE public.loan_payments;
DROP TABLE public.loan_applications;
DROP TABLE public.documents;
DROP SEQUENCE public.batch_step_execution_seq;
DROP TABLE public.batch_step_execution_context;
DROP TABLE public.batch_step_execution;
DROP SEQUENCE public.batch_job_seq;
DROP TABLE public.batch_job_instance;
DROP SEQUENCE public.batch_job_execution_seq;
DROP TABLE public.batch_job_execution_params;
DROP TABLE public.batch_job_execution_context;
DROP TABLE public.batch_job_execution;
DROP TABLE public.bank_accounts;
DROP TABLE public.audit_logs;
SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 224 (class 1259 OID 24917)
-- Name: audit_logs; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.audit_logs (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    action text NOT NULL,
    created_at time with time zone,
    account_number character varying(255)
);


--
-- TOC entry 225 (class 1259 OID 41233)
-- Name: audit_logs_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.audit_logs ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.audit_logs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 222 (class 1259 OID 24893)
-- Name: bank_accounts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.bank_accounts (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    bank_name character varying(255) NOT NULL,
    account_number character varying(255) NOT NULL,
    iban character varying(255) NOT NULL,
    currency character varying(255) NOT NULL,
    created_at time with time zone
);


--
-- TOC entry 226 (class 1259 OID 41234)
-- Name: bank_accounts_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.bank_accounts ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.bank_accounts_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 231 (class 1259 OID 66262)
-- Name: batch_job_execution; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.batch_job_execution (
    job_execution_id bigint NOT NULL,
    version bigint,
    job_instance_id bigint NOT NULL,
    create_time timestamp without time zone NOT NULL,
    start_time timestamp without time zone,
    end_time timestamp without time zone,
    status character varying(10),
    exit_code character varying(2500),
    exit_message character varying(2500),
    last_updated timestamp without time zone
);


--
-- TOC entry 235 (class 1259 OID 66308)
-- Name: batch_job_execution_context; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.batch_job_execution_context (
    job_execution_id bigint NOT NULL,
    short_context character varying(2500) NOT NULL,
    serialized_context text
);


--
-- TOC entry 232 (class 1259 OID 66274)
-- Name: batch_job_execution_params; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.batch_job_execution_params (
    job_execution_id bigint NOT NULL,
    parameter_name character varying(100) NOT NULL,
    parameter_type character varying(100) NOT NULL,
    parameter_value character varying(2500),
    identifying character(1) NOT NULL
);


--
-- TOC entry 237 (class 1259 OID 66321)
-- Name: batch_job_execution_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.batch_job_execution_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 230 (class 1259 OID 66255)
-- Name: batch_job_instance; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.batch_job_instance (
    job_instance_id bigint NOT NULL,
    version bigint,
    job_name character varying(100) NOT NULL,
    job_key character varying(32) NOT NULL
);


--
-- TOC entry 238 (class 1259 OID 66322)
-- Name: batch_job_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.batch_job_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 233 (class 1259 OID 66284)
-- Name: batch_step_execution; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.batch_step_execution (
    step_execution_id bigint NOT NULL,
    version bigint NOT NULL,
    step_name character varying(100) NOT NULL,
    job_execution_id bigint NOT NULL,
    create_time timestamp without time zone NOT NULL,
    start_time timestamp without time zone,
    end_time timestamp without time zone,
    status character varying(10),
    commit_count bigint,
    read_count bigint,
    filter_count bigint,
    write_count bigint,
    read_skip_count bigint,
    write_skip_count bigint,
    process_skip_count bigint,
    rollback_count bigint,
    exit_code character varying(2500),
    exit_message character varying(2500),
    last_updated timestamp without time zone
);


--
-- TOC entry 234 (class 1259 OID 66296)
-- Name: batch_step_execution_context; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.batch_step_execution_context (
    step_execution_id bigint NOT NULL,
    short_context character varying(2500) NOT NULL,
    serialized_context text
);


--
-- TOC entry 236 (class 1259 OID 66320)
-- Name: batch_step_execution_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.batch_step_execution_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 221 (class 1259 OID 24878)
-- Name: documents; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.documents (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    application_id bigint NOT NULL,
    document_type character varying(255),
    file_path character varying(255),
    uploaded_at time with time zone
);


--
-- TOC entry 227 (class 1259 OID 41235)
-- Name: documents_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.documents ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.documents_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 215 (class 1259 OID 24826)
-- Name: loan_applications; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.loan_applications (
    id bigint NOT NULL,
    user_id bigint,
    amount double precision NOT NULL,
    term bigint NOT NULL,
    created_at time with time zone
);


--
-- TOC entry 218 (class 1259 OID 24847)
-- Name: loan_applications_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.loan_applications ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.loan_applications_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 220 (class 1259 OID 24858)
-- Name: loan_payments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.loan_payments (
    id bigint NOT NULL,
    application_id bigint NOT NULL,
    due_date date NOT NULL,
    amount double precision NOT NULL,
    status character varying(255) NOT NULL,
    paid_date time with time zone
);


--
-- TOC entry 229 (class 1259 OID 57615)
-- Name: loan_payments_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.loan_payments ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.loan_payments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 216 (class 1259 OID 24836)
-- Name: loan_scoring; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.loan_scoring (
    id bigint NOT NULL,
    application_id bigint NOT NULL,
    income double precision NOT NULL,
    credit_score double precision NOT NULL,
    calculated_score double precision NOT NULL,
    decision character varying(255) NOT NULL
);


--
-- TOC entry 217 (class 1259 OID 24846)
-- Name: loan_scoring_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.loan_scoring ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.loan_scoring_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 223 (class 1259 OID 24905)
-- Name: notifications; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.notifications (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    message text NOT NULL,
    status character varying(255) NOT NULL,
    created_at time with time zone
);


--
-- TOC entry 228 (class 1259 OID 49423)
-- Name: notifications_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.notifications ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.notifications_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 214 (class 1259 OID 24817)
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    password_hash character varying(255) NOT NULL,
    user_role character varying(255),
    created_at time with time zone,
    credit_score double precision,
    income double precision
);


--
-- TOC entry 219 (class 1259 OID 24848)
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.users ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 3434 (class 0 OID 24917)
-- Dependencies: 224
-- Data for Name: audit_logs; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.audit_logs (id, user_id, action, created_at, account_number) FROM stdin;
1	1	Kullan─▒c─▒ ba┼şar─▒yla kaydedildi	\N	\N
2	2	Kullan─▒c─▒ ba┼şar─▒yla kaydedildi	\N	\N
3	3	Kullan─▒c─▒ ba┼şar─▒yla kaydedildi	\N	\N
4	1	Kullan─▒c─▒ ba┼şar─▒yla g├╝ncellendi	\N	\N
7	2	Kullan─▒c─▒ ba┼şar─▒yla g├╝ncellendi	\N	\N
8	1	BANKA HESABI BA┼ŞARIYLA OLU┼ŞTURULDU	\N	1234567891234567
9	1	BANKA HESABI BA┼ŞARIYLA OLU┼ŞTURULDU	\N	0234567891234567
10	1	BANKA HESABI BA┼ŞARIYLA OLU┼ŞTURULDU	\N	02345678912
11	1	KRED─░ BA┼ŞVURUSU YAPILDI	21:09:14.595996+00	\N
12	1	KRED─░ BA┼ŞVURUSU YAPILDI	21:09:21.846368+00	\N
13	3	KRED─░ BA┼ŞVURUSU YAPILDI	21:17:17.820086+00	\N
14	1	KRED─░ BA┼ŞVURUSU YAPILDI	14:01:49.212965+00	\N
15	1	KRED─░ BA┼ŞVURUSU YAPILDI	14:31:14.631956+00	\N
16	1	KRED─░ BA┼ŞVURUSU REDDED─░LD─░	22:08:10.262827+00	\N
17	1	├ûDEME BA┼ŞARIYLA TAMAMLANDI	14:08:38.253373+00	\N
18	6	Kullan─▒c─▒ ba┼şar─▒yla kaydedildi	21:20:31.109007+00	\N
19	6	KRED─░ BA┼ŞVURUSU YAPILDI	10:49:52.229452+00	\N
34	1	KRED─░ BA┼ŞVURUSU YAPILDI	11:50:26.050554+00	\N
35	1	KRED─░ BA┼ŞVURUSU YAPILDI	11:50:42.672696+00	\N
36	1	KRED─░ BA┼ŞVURUSU YAPILDI	11:52:18.06728+00	\N
43	1	BELGE BA┼ŞARYLA Y├£KLEND─░	13:49:54.9432+00	\N
44	1	BELGE G├£NCELLEND─░	14:09:45.400859+00	\N
45	1	BELGE G├£NCELLEND─░	14:10:28.342331+00	\N
46	6	BELGE BA┼ŞARYLA Y├£KLEND─░	14:11:20.075082+00	\N
\.


--
-- TOC entry 3432 (class 0 OID 24893)
-- Dependencies: 222
-- Data for Name: bank_accounts; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.bank_accounts (id, user_id, bank_name, account_number, iban, currency, created_at) FROM stdin;
1	1	─░s Bankas─▒	1234567891234567	TR0001000200030004	TRY	\N
2	1	A Bankas─▒	0234567891234567	TR000100020003000400050006	TRY	\N
3	1	B Bankas─▒	02345678912	TR0001000200030004000500060	TRY	\N
\.


--
-- TOC entry 3441 (class 0 OID 66262)
-- Dependencies: 231
-- Data for Name: batch_job_execution; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.batch_job_execution (job_execution_id, version, job_instance_id, create_time, start_time, end_time, status, exit_code, exit_message, last_updated) FROM stdin;
7	2	7	2025-05-22 23:59:13.427201	2025-05-22 23:59:13.472715	2025-05-22 23:59:13.621646	COMPLETED	COMPLETED		2025-05-22 23:59:13.621646
8	2	8	2025-05-25 17:04:49.244241	2025-05-25 17:04:49.388188	2025-05-25 17:04:50.096701	COMPLETED	COMPLETED		2025-05-25 17:04:50.096701
\.


--
-- TOC entry 3445 (class 0 OID 66308)
-- Dependencies: 235
-- Data for Name: batch_job_execution_context; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.batch_job_execution_context (job_execution_id, short_context, serialized_context) FROM stdin;
7	rO0ABXNyABFqYXZhLnV0aWwuSGFzaE1hcAUH2sHDFmDRAwACRgAKbG9hZEZhY3RvckkACXRocmVzaG9sZHhwP0AAAAAAAAx3CAAAABAAAAABdAANYmF0Y2gudmVyc2lvbnQABTUuMS4weA==	\N
8	rO0ABXNyABFqYXZhLnV0aWwuSGFzaE1hcAUH2sHDFmDRAwACRgAKbG9hZEZhY3RvckkACXRocmVzaG9sZHhwP0AAAAAAAAx3CAAAABAAAAABdAANYmF0Y2gudmVyc2lvbnQABTUuMS4weA==	\N
\.


--
-- TOC entry 3442 (class 0 OID 66274)
-- Dependencies: 232
-- Data for Name: batch_job_execution_params; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.batch_job_execution_params (job_execution_id, parameter_name, parameter_type, parameter_value, identifying) FROM stdin;
7	startAt	java.lang.Long	1747947553377	Y
8	startAt	java.lang.Long	1748181889114	Y
\.


--
-- TOC entry 3440 (class 0 OID 66255)
-- Dependencies: 230
-- Data for Name: batch_job_instance; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.batch_job_instance (job_instance_id, version, job_name, job_key) FROM stdin;
7	0	loanPaymentJob	d90db836ade255a0a9feace121b4bc2d
8	0	loanPaymentJob	5eeb55187a599cdfbdf78d8f96ec36d7
\.


--
-- TOC entry 3443 (class 0 OID 66284)
-- Dependencies: 233
-- Data for Name: batch_step_execution; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.batch_step_execution (step_execution_id, version, step_name, job_execution_id, create_time, start_time, end_time, status, commit_count, read_count, filter_count, write_count, read_skip_count, write_skip_count, process_skip_count, rollback_count, exit_code, exit_message, last_updated) FROM stdin;
7	3	processLoanPaymentsStep	7	2025-05-22 23:59:13.497518	2025-05-22 23:59:13.5082	2025-05-22 23:59:13.608667	COMPLETED	1	0	0	0	0	0	0	0	COMPLETED		2025-05-22 23:59:13.609667
8	3	processLoanPaymentsStep	8	2025-05-25 17:04:49.422736	2025-05-25 17:04:49.431184	2025-05-25 17:04:50.079954	COMPLETED	1	4	0	4	0	0	0	0	COMPLETED		2025-05-25 17:04:50.081957
\.


--
-- TOC entry 3444 (class 0 OID 66296)
-- Dependencies: 234
-- Data for Name: batch_step_execution_context; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.batch_step_execution_context (step_execution_id, short_context, serialized_context) FROM stdin;
7	rO0ABXNyABFqYXZhLnV0aWwuSGFzaE1hcAUH2sHDFmDRAwACRgAKbG9hZEZhY3RvckkACXRocmVzaG9sZHhwP0AAAAAAAAx3CAAAABAAAAAEdAARYmF0Y2gudGFza2xldFR5cGV0AD1vcmcuc3ByaW5nZnJhbWV3b3JrLmJhdGNoLmNvcmUuc3RlcC5pdGVtLkNodW5rT3JpZW50ZWRUYXNrbGV0dAANYmF0Y2gudmVyc2lvbnQABTUuMS4wdAAeSnBhUGFnaW5nSXRlbVJlYWRlci5yZWFkLmNvdW50c3IAEWphdmEubGFuZy5JbnRlZ2VyEuKgpPeBhzgCAAFJAAV2YWx1ZXhyABBqYXZhLmxhbmcuTnVtYmVyhqyVHQuU4IsCAAB4cAAAAAF0AA5iYXRjaC5zdGVwVHlwZXQAN29yZy5zcHJpbmdmcmFtZXdvcmsuYmF0Y2guY29yZS5zdGVwLnRhc2tsZXQuVGFza2xldFN0ZXB4	\N
8	rO0ABXNyABFqYXZhLnV0aWwuSGFzaE1hcAUH2sHDFmDRAwACRgAKbG9hZEZhY3RvckkACXRocmVzaG9sZHhwP0AAAAAAAAx3CAAAABAAAAAEdAARYmF0Y2gudGFza2xldFR5cGV0AD1vcmcuc3ByaW5nZnJhbWV3b3JrLmJhdGNoLmNvcmUuc3RlcC5pdGVtLkNodW5rT3JpZW50ZWRUYXNrbGV0dAANYmF0Y2gudmVyc2lvbnQABTUuMS4wdAAeSnBhUGFnaW5nSXRlbVJlYWRlci5yZWFkLmNvdW50c3IAEWphdmEubGFuZy5JbnRlZ2VyEuKgpPeBhzgCAAFJAAV2YWx1ZXhyABBqYXZhLmxhbmcuTnVtYmVyhqyVHQuU4IsCAAB4cAAAAAV0AA5iYXRjaC5zdGVwVHlwZXQAN29yZy5zcHJpbmdmcmFtZXdvcmsuYmF0Y2guY29yZS5zdGVwLnRhc2tsZXQuVGFza2xldFN0ZXB4	\N
\.


--
-- TOC entry 3431 (class 0 OID 24878)
-- Dependencies: 221
-- Data for Name: documents; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.documents (id, user_id, application_id, document_type, file_path, uploaded_at) FROM stdin;
1	1	21	kimlik	/path/to/documentUpdate.pdf	14:10:28.334341+00
\.


--
-- TOC entry 3425 (class 0 OID 24826)
-- Dependencies: 215
-- Data for Name: loan_applications; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.loan_applications (id, user_id, amount, term, created_at) FROM stdin;
1	1	50000	12	21:09:14.441106+00
2	1	50	12	21:09:21.83893+00
3	3	50000000	12	21:17:17.806132+00
4	1	10000000	12	14:01:49.144695+00
5	1	500	3	14:31:14.552146+00
6	6	100000000	36	10:49:52.18454+00
19	1	10000	3	11:50:26.039001+00
20	1	5000	12	11:50:42.666693+00
21	1	3000	3	11:52:17.999758+00
\.


--
-- TOC entry 3430 (class 0 OID 24858)
-- Dependencies: 220
-- Data for Name: loan_payments; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.loan_payments (id, application_id, due_date, amount, status, paid_date) FROM stdin;
5	1	1970-05-01	4166.666666666667	PENDING	\N
6	1	1970-06-01	4166.666666666667	PENDING	\N
7	1	1970-07-01	4166.666666666667	PENDING	\N
8	1	1970-08-01	4166.666666666667	PENDING	\N
9	1	1970-09-01	4166.666666666667	PENDING	\N
10	1	1970-10-01	4166.666666666667	PENDING	\N
11	1	1970-11-01	4166.666666666667	PENDING	\N
12	1	1970-12-01	4166.666666666667	PENDING	\N
13	2	1970-01-01	4.166666666666667	PENDING	\N
14	2	1970-02-01	4.166666666666667	PENDING	\N
15	2	1970-03-01	4.166666666666667	PENDING	\N
16	2	1970-04-01	4.166666666666667	PENDING	\N
17	2	1970-05-01	4.166666666666667	PENDING	\N
18	2	1970-06-01	4.166666666666667	PENDING	\N
19	2	1970-07-01	4.166666666666667	PENDING	\N
20	2	1970-08-01	4.166666666666667	PENDING	\N
21	2	1970-09-01	4.166666666666667	PENDING	\N
22	2	1970-10-01	4.166666666666667	PENDING	\N
23	2	1970-11-01	4.166666666666667	PENDING	\N
24	2	1970-12-01	4.166666666666667	PENDING	\N
25	3	1970-01-01	4166666.6666666665	PENDING	\N
26	3	1970-02-01	4166666.6666666665	PENDING	\N
27	3	1970-03-01	4166666.6666666665	PENDING	\N
28	3	1970-04-01	4166666.6666666665	PENDING	\N
29	3	1970-05-01	4166666.6666666665	PENDING	\N
30	3	1970-06-01	4166666.6666666665	PENDING	\N
31	3	1970-07-01	4166666.6666666665	PENDING	\N
32	3	1970-08-01	4166666.6666666665	PENDING	\N
33	3	1970-09-01	4166666.6666666665	PENDING	\N
34	3	1970-10-01	4166666.6666666665	PENDING	\N
35	3	1970-11-01	4166666.6666666665	PENDING	\N
36	3	1970-12-01	4166666.6666666665	PENDING	\N
37	4	1970-01-01	833333.3333333334	PENDING	\N
38	4	1970-02-01	833333.3333333334	PENDING	\N
39	4	1970-03-01	833333.3333333334	PENDING	\N
40	4	1970-04-01	833333.3333333334	PENDING	\N
41	4	1970-05-01	833333.3333333334	PENDING	\N
42	4	1970-06-01	833333.3333333334	PENDING	\N
43	4	1970-07-01	833333.3333333334	PENDING	\N
44	4	1970-08-01	833333.3333333334	PENDING	\N
45	4	1970-09-01	833333.3333333334	PENDING	\N
46	4	1970-10-01	833333.3333333334	PENDING	\N
47	4	1970-11-01	833333.3333333334	PENDING	\N
48	4	1970-12-01	833333.3333333334	PENDING	\N
1	1	1970-01-01	4166.666666666667	PAID	14:08:38.15291+00
2	1	1970-02-01	4166.666666666667	PAID	\N
3	1	1970-03-01	4166.666666666667	PAID	17:14:53.818551+00
4	1	1970-04-01	4166.666666666667	LATE	\N
\.


--
-- TOC entry 3426 (class 0 OID 24836)
-- Dependencies: 216
-- Data for Name: loan_scoring; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.loan_scoring (id, application_id, income, credit_score, calculated_score, decision) FROM stdin;
1	1	90000	800	92.47058823529412	APPROVED
2	2	90000	800	92.47058823529412	APPROVED
3	3	40000	700	65.41176470588235	APPROVED
4	4	90000	800	92.47058823529412	APPROVED
5	5	90000	800	92.47058823529412	REJECTED
12	6	45000	500	53.294117647058826	APPROVED
20	19	90000	800	92.47058823529412	APPROVED
21	20	90000	800	92.47058823529412	APPROVED
22	21	90000	800	92.47058823529412	APPROVED
\.


--
-- TOC entry 3433 (class 0 OID 24905)
-- Dependencies: 223
-- Data for Name: notifications; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.notifications (id, user_id, message, status, created_at) FROM stdin;
3	3	Kayd─▒n─▒z ba┼şar─▒yla tamamlanm─▒┼şt─▒r.	SENT	11:05:02.52312+00
17	2	G├╝ncellemeniz ba┼şar─▒yla tamamlanm─▒┼şt─▒r.	SENT	16:04:51.113041+00
18	1	Yeni banka hesab─▒n─▒z ba┼şar─▒yla olu┼şturuldu. IBAN: TR0001000200030004	SENT	16:46:15.150567+00
19	1	Yeni banka hesab─▒n─▒z ba┼şar─▒yla olu┼şturuldu. IBAN: TR000100020003000400050006	SENT	17:02:11.861264+00
20	1	Yeni banka hesab─▒n─▒z ba┼şar─▒yla olu┼şturuldu. IBAN: TR0001000200030004000500060	SENT	20:15:31.735248+00
21	1	Kredi ba┼şvurunuz ba┼şar─▒yla al─▒nm─▒┼şt─▒r. Ba┼şvuru ID: 1	SENT	21:09:14.791568+00
22	1	Kredi ba┼şvurunuz ba┼şar─▒yla al─▒nm─▒┼şt─▒r. Ba┼şvuru ID: 2	SENT	21:09:21.878099+00
23	3	Kredi ba┼şvurunuz ba┼şar─▒yla al─▒nm─▒┼şt─▒r. Ba┼şvuru ID: 3	SENT	21:17:17.869554+00
24	1	Kredi ba┼şvurunuz ba┼şar─▒yla al─▒nm─▒┼şt─▒r. Ba┼şvuru ID: 4	SENT	14:01:49.309505+00
25	1	Kredi ba┼şvurunuz ba┼şar─▒yla al─▒nm─▒┼şt─▒r. Ba┼şvuru ID: 5	SENT	14:31:14.730847+00
26	1	├ûdeme ba┼şar─▒yla tamamland─▒. 	SENT	14:08:38.26101+00
27	6	Kayd─▒n─▒z ba┼şar─▒yla tamamlanm─▒┼şt─▒r.	SENT	21:20:31.168989+00
1	1	Kayd─▒n─▒z ba┼şar─▒yla tamamlanm─▒┼şt─▒r.	READ	13:24:42.204806+00
4	1	G├╝ncellemeniz ba┼şar─▒yla tamamlanm─▒┼şt─▒r.	READ	17:08:03.357952+00
28	6	Kredi ba┼şvurunuz ba┼şar─▒yla al─▒nm─▒┼şt─▒r. Ba┼şvuru ID: 6	SENT	10:49:52.342126+00
43	1	Kredi ba┼şvurunuz ba┼şar─▒yla al─▒nm─▒┼şt─▒r. Ba┼şvuru ID: 19	SENT	11:50:26.103678+00
44	1	Kredi ba┼şvurunuz ba┼şar─▒yla al─▒nm─▒┼şt─▒r. Ba┼şvuru ID: 20	SENT	11:50:42.690681+00
45	1	Kredi ba┼şvurunuz ba┼şar─▒yla al─▒nm─▒┼şt─▒r. Ba┼şvuru ID: 21	SENT	11:52:18.149909+00
\.


--
-- TOC entry 3424 (class 0 OID 24817)
-- Dependencies: 214
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.users (id, name, email, password_hash, user_role, created_at, credit_score, income) FROM stdin;
3	Ali Seccad	aliSeccad@example.com	$2a$10$TVdWftCaHO8B8Nk6Q2bTPem3Yi9viLWjc67bCeO3cJArjog/kbndC	ADMIN	11:05:02.353863+00	700	40000
1	fatih taylan	fatihdeneme@example.com	$2a$10$tYMCnn0AWOmhghJif89CreYu8e3PJz0ti/S2XnwaPa.0HawUOE/G2	CUSTOMER	\N	800	90000
2	beyzanur ertem	beyza@example.com	$2a$10$Ib0BryJMIPL.bamXim9Kfei1TQDel5N2NRW1/HLMlHMZ76eW1vPlm	ADMIN	\N	700	45000
6	fatih samed taylan	fatihdeneme2@example.com	$2a$10$g0.v7iP8eIKWHJNfsGmRmumR/rQ2PxzsUPAjh8nOA7FFqA.2n0hgy	CUSTOMER	21:20:30.972948+00	500	45000
\.


--
-- TOC entry 3454 (class 0 OID 0)
-- Dependencies: 225
-- Name: audit_logs_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.audit_logs_id_seq', 54, true);


--
-- TOC entry 3455 (class 0 OID 0)
-- Dependencies: 226
-- Name: bank_accounts_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.bank_accounts_id_seq', 7, true);


--
-- TOC entry 3456 (class 0 OID 0)
-- Dependencies: 237
-- Name: batch_job_execution_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.batch_job_execution_seq', 9, false);


--
-- TOC entry 3457 (class 0 OID 0)
-- Dependencies: 238
-- Name: batch_job_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.batch_job_seq', 9, false);


--
-- TOC entry 3458 (class 0 OID 0)
-- Dependencies: 236
-- Name: batch_step_execution_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.batch_step_execution_seq', 9, false);


--
-- TOC entry 3459 (class 0 OID 0)
-- Dependencies: 227
-- Name: documents_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.documents_id_seq', 6, true);


--
-- TOC entry 3460 (class 0 OID 0)
-- Dependencies: 218
-- Name: loan_applications_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.loan_applications_id_seq', 33, true);


--
-- TOC entry 3461 (class 0 OID 0)
-- Dependencies: 229
-- Name: loan_payments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.loan_payments_id_seq', 48, true);


--
-- TOC entry 3462 (class 0 OID 0)
-- Dependencies: 217
-- Name: loan_scoring_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.loan_scoring_id_seq', 30, true);


--
-- TOC entry 3463 (class 0 OID 0)
-- Dependencies: 228
-- Name: notifications_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.notifications_id_seq', 58, true);


--
-- TOC entry 3464 (class 0 OID 0)
-- Dependencies: 219
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.users_id_seq', 27, true);


--
-- TOC entry 3256 (class 2606 OID 24923)
-- Name: audit_logs audit_logs_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.audit_logs
    ADD CONSTRAINT audit_logs_pkey PRIMARY KEY (id);


--
-- TOC entry 3250 (class 2606 OID 32979)
-- Name: bank_accounts bank_accounts_account_number_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bank_accounts
    ADD CONSTRAINT bank_accounts_account_number_key UNIQUE (account_number);


--
-- TOC entry 3252 (class 2606 OID 24897)
-- Name: bank_accounts bank_accounts_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bank_accounts
    ADD CONSTRAINT bank_accounts_pkey PRIMARY KEY (id);


--
-- TOC entry 3268 (class 2606 OID 66314)
-- Name: batch_job_execution_context batch_job_execution_context_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.batch_job_execution_context
    ADD CONSTRAINT batch_job_execution_context_pkey PRIMARY KEY (job_execution_id);


--
-- TOC entry 3262 (class 2606 OID 66268)
-- Name: batch_job_execution batch_job_execution_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.batch_job_execution
    ADD CONSTRAINT batch_job_execution_pkey PRIMARY KEY (job_execution_id);


--
-- TOC entry 3258 (class 2606 OID 66259)
-- Name: batch_job_instance batch_job_instance_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.batch_job_instance
    ADD CONSTRAINT batch_job_instance_pkey PRIMARY KEY (job_instance_id);


--
-- TOC entry 3266 (class 2606 OID 66302)
-- Name: batch_step_execution_context batch_step_execution_context_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.batch_step_execution_context
    ADD CONSTRAINT batch_step_execution_context_pkey PRIMARY KEY (step_execution_id);


--
-- TOC entry 3264 (class 2606 OID 66290)
-- Name: batch_step_execution batch_step_execution_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.batch_step_execution
    ADD CONSTRAINT batch_step_execution_pkey PRIMARY KEY (step_execution_id);


--
-- TOC entry 3248 (class 2606 OID 24882)
-- Name: documents documents_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.documents
    ADD CONSTRAINT documents_pkey PRIMARY KEY (id);


--
-- TOC entry 3260 (class 2606 OID 66261)
-- Name: batch_job_instance job_inst_un; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.batch_job_instance
    ADD CONSTRAINT job_inst_un UNIQUE (job_name, job_key);


--
-- TOC entry 3240 (class 2606 OID 24830)
-- Name: loan_applications loan_applications_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loan_applications
    ADD CONSTRAINT loan_applications_pkey PRIMARY KEY (id);


--
-- TOC entry 3246 (class 2606 OID 24862)
-- Name: loan_payments loan_payments_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loan_payments
    ADD CONSTRAINT loan_payments_pkey PRIMARY KEY (id);


--
-- TOC entry 3242 (class 2606 OID 24840)
-- Name: loan_scoring loan_scoring_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loan_scoring
    ADD CONSTRAINT loan_scoring_pkey PRIMARY KEY (id);


--
-- TOC entry 3254 (class 2606 OID 24911)
-- Name: notifications notifications_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_pkey PRIMARY KEY (id);


--
-- TOC entry 3244 (class 2606 OID 74594)
-- Name: loan_scoring uq_loan_scoring_application; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loan_scoring
    ADD CONSTRAINT uq_loan_scoring_application UNIQUE (application_id);


--
-- TOC entry 3236 (class 2606 OID 24825)
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- TOC entry 3238 (class 2606 OID 24823)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 3272 (class 2606 OID 24888)
-- Name: documents application; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.documents
    ADD CONSTRAINT application FOREIGN KEY (application_id) REFERENCES public.loan_applications(id);


--
-- TOC entry 3276 (class 2606 OID 74588)
-- Name: audit_logs audit_logs_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.audit_logs
    ADD CONSTRAINT audit_logs_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 3274 (class 2606 OID 24898)
-- Name: bank_accounts bank_accounts_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bank_accounts
    ADD CONSTRAINT bank_accounts_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- TOC entry 3281 (class 2606 OID 66315)
-- Name: batch_job_execution_context job_exec_ctx_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.batch_job_execution_context
    ADD CONSTRAINT job_exec_ctx_fk FOREIGN KEY (job_execution_id) REFERENCES public.batch_job_execution(job_execution_id);


--
-- TOC entry 3278 (class 2606 OID 66279)
-- Name: batch_job_execution_params job_exec_params_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.batch_job_execution_params
    ADD CONSTRAINT job_exec_params_fk FOREIGN KEY (job_execution_id) REFERENCES public.batch_job_execution(job_execution_id);


--
-- TOC entry 3279 (class 2606 OID 66291)
-- Name: batch_step_execution job_exec_step_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.batch_step_execution
    ADD CONSTRAINT job_exec_step_fk FOREIGN KEY (job_execution_id) REFERENCES public.batch_job_execution(job_execution_id);


--
-- TOC entry 3277 (class 2606 OID 66269)
-- Name: batch_job_execution job_inst_exec_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.batch_job_execution
    ADD CONSTRAINT job_inst_exec_fk FOREIGN KEY (job_instance_id) REFERENCES public.batch_job_instance(job_instance_id);


--
-- TOC entry 3269 (class 2606 OID 24831)
-- Name: loan_applications loan_applications_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loan_applications
    ADD CONSTRAINT loan_applications_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- TOC entry 3271 (class 2606 OID 24863)
-- Name: loan_payments loan_payments_application_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loan_payments
    ADD CONSTRAINT loan_payments_application_id_fkey FOREIGN KEY (application_id) REFERENCES public.loan_applications(id);


--
-- TOC entry 3270 (class 2606 OID 24841)
-- Name: loan_scoring loan_scoring_application_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loan_scoring
    ADD CONSTRAINT loan_scoring_application_id_fkey FOREIGN KEY (application_id) REFERENCES public.loan_applications(id);


--
-- TOC entry 3275 (class 2606 OID 24912)
-- Name: notifications notifications_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- TOC entry 3280 (class 2606 OID 66303)
-- Name: batch_step_execution_context step_exec_ctx_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.batch_step_execution_context
    ADD CONSTRAINT step_exec_ctx_fk FOREIGN KEY (step_execution_id) REFERENCES public.batch_step_execution(step_execution_id);


--
-- TOC entry 3273 (class 2606 OID 24883)
-- Name: documents user; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.documents
    ADD CONSTRAINT "user" FOREIGN KEY (user_id) REFERENCES public.users(id);


-- Completed on 2025-05-30 15:10:47

--
-- PostgreSQL database dump complete
--

