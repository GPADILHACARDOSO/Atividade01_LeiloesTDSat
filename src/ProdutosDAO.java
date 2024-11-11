/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Adm
 */

import java.sql.PreparedStatement;
import java.sql.Connection;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class ProdutosDAO {
    
    Connection conn;
    PreparedStatement prep;
    ResultSet resultset;
    ArrayList<ProdutosDTO> listagem = new ArrayList<>();
    
    public void cadastrarProduto (ProdutosDTO produto){
       	String sql = "INSERT INTO produtos (nome, valor, status) VALUES (?, ?, ?)";

        try {
            conn = new conectaDAO().connectDB();
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Conexão falhou. " + "Erro ao realizar o cadastro de produto.");
                return;
            }

            prep = conn.prepareStatement(sql);
            prep.setString(1, produto.getNome());
            prep.setInt(2, produto.getValor());
            prep.setString(3, produto.getStatus());

            int rowsAffected = prep.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Produto cadastrado com sucesso!");
            } else {
                JOptionPane.showMessageDialog(null, "Erro ao realizar o cadastro de produto.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar produto: " + e.getMessage());
        } finally {
            try {
                if (prep != null) prep.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Erro ao fechar conexão: " + e.getMessage());
            }
        }
        
    }
    
    public ArrayList<ProdutosDTO> listarProdutos() {
            ArrayList<ProdutosDTO> produtos = new ArrayList<>();
            Connection conn = new conectaDAO().connectDB();

            try {
                String sql = "SELECT id, nome, valor, status FROM produtos";
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    ProdutosDTO produto = new ProdutosDTO();
                    produto.setId(rs.getInt("id"));
                    produto.setNome(rs.getString("nome"));
                    produto.setValor(rs.getInt("valor"));
                    produto.setStatus(rs.getString("status"));
                    produtos.add(produto);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return produtos;
        }

        public void venderProduto(int idProduto) {
        String verificarStatusSQL = "SELECT status FROM produtos WHERE id = ?";
        String atualizarStatusSQL = "UPDATE produtos SET status = 'Vendido' WHERE id = ?";

        try (Connection conn = new conectaDAO().connectDB();
             PreparedStatement verificarStmt = conn.prepareStatement(verificarStatusSQL)) {

            // Verifica o status do produto antes de qualquer alteração
            verificarStmt.setInt(1, idProduto);
            ResultSet rs = verificarStmt.executeQuery();

            if (rs.next()) {
                String statusAtual = rs.getString("status");

                if ("Vendido".equalsIgnoreCase(statusAtual)) {
                    // Sai do metodo pois o produto ja esta vendido
                    JOptionPane.showMessageDialog(null, "Produto já vendido.");
                    return;
                }
            } else {
                // Sai do metodo pois o produto n foi encontrado
                JOptionPane.showMessageDialog(null, "Produto não encontrado.");
                return;
            }

            // Atualiza o status para "Vendido" caso esteja "A Venda"
            try (PreparedStatement atualizarStmt = conn.prepareStatement(atualizarStatusSQL)) {
                atualizarStmt.setInt(1, idProduto);
                int rowsAffected = atualizarStmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Produto vendido com sucesso!");
                } else {
                    JOptionPane.showMessageDialog(null, "Falha ao vender produto.");
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Falha ao vender produto: " + e.getMessage());
            e.printStackTrace();
        }
    }
        
    public ArrayList<ProdutosDTO> listarProdutosVendidos() {
        ArrayList<ProdutosDTO> produtosVendidos = new ArrayList<>();
        String sql = "SELECT * FROM produtos WHERE status = 'Vendido'";

        try (Connection conn = new conectaDAO().connectDB();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ProdutosDTO produto = new ProdutosDTO();
                produto.setId(rs.getInt("id"));
                produto.setNome(rs.getString("nome"));
                produto.setValor(rs.getInt("valor"));
                produto.setStatus(rs.getString("status"));
                produtosVendidos.add(produto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produtosVendidos;
    }
    
}

