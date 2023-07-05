package no.nav.tiltakspenger.soknad.api

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

// Testklasse for å se om CodeQL plukker opp sårbar kode!

class Vulnerable {
    private var conn: Connection? = null
    private var stmt: Statement? = null
    private var rs: ResultSet? = null

    fun connectDb() {
        val url = "jdbc:mysql://localhost:3306/testDB"
        val user = "root"
        val password = "password"

        conn = DriverManager.getConnection(url, user, password)
        stmt = conn!!.createStatement()
    }

    fun getUserByUsername(username: String): ResultSet? {
        rs = stmt!!.executeQuery("SELECT * FROM users WHERE username = '$username'")

        return rs
    }
}
