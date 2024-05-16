import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GraphPanel extends JPanel implements MouseListener, MouseMotionListener {
    private ArrayList<Node> nodes = new ArrayList<>();
    private ArrayList<Edge> edges = new ArrayList<>();
    private Node selectedNode = null;
    private Graph graph = new Graph(); // Create an instance of the Graph class
    private boolean deletingNode = false;
    private Node rightClickedNode = null;

    public GraphPanel() {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        setPreferredSize(new Dimension(800, 600));

        // Add button for checking hamiltonian cyle
        JButton checkHamiltonButton = new JButton("Check Hamiltonian Cycle");
        checkHamiltonButton.addActionListener(e -> checkHamiltonianCycle());
        add(checkHamiltonButton);

        // Add button for instant deletion
        JButton clearButton = new JButton("Clear Drawing");
        clearButton.addActionListener(e -> clearDrawing());
        add(clearButton);

        // Hold "D" on the keyboard for deleting a node
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_D) {
                    deletingNode = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_D) {
                    deletingNode = false;
                }
            }
        });
    }

    private void checkHamiltonianCycle() {
        // Check if the user input is a graph or not
        if (nodes.size() < 3 || edges.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please complete the graph before checking for a Hamiltonian cycle.");
            return;
        }
    
        ArrayList<int[]> listOfEdges = new ArrayList<>();
        for (Edge edge : edges) {
            int[] edgeArray = {nodes.indexOf(edge.start), nodes.indexOf(edge.end)};
            listOfEdges.add(edgeArray);
        }
        graph = new Graph(listOfEdges);
        String hamiltonCycle = graph.getHamiltonCycle();
        // No cycle found
        if (hamiltonCycle.equals("Not possible")) {
            JOptionPane.showMessageDialog(this, "No Hamiltonian cycle found.");
        } else {
            String[] labels = nodes.stream().map(node -> node.label).toArray(String[]::new);
            String[] cycleIndices = hamiltonCycle.split("-");
            StringBuilder cycleLabels = new StringBuilder();
            for (String index : cycleIndices) {
                cycleLabels.append(labels[Integer.parseInt(index)]);
                cycleLabels.append("-");
            }
            cycleLabels.deleteCharAt(cycleLabels.length() - 1); // Remove the last "-"
            // Show information how the Hamiltonian Cycle is traverse
            JOptionPane.showMessageDialog(this, "Hamiltonian cycle: " + cycleLabels.toString());
        }
    }
    
    // Delete drawing
    private void clearDrawing() {
        nodes.clear();
        edges.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Edge edge : edges) {
            edge.draw(g);
        }
        for (Node node : nodes) {
            node.draw(g);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            Node foundNode = findNode(e.getPoint());
            if (foundNode != null) {
                if (rightClickedNode == null) {
                    rightClickedNode = foundNode;
                } else if (!rightClickedNode.equals(foundNode)) {
                    edges.add(new Edge(rightClickedNode, foundNode));
                    rightClickedNode = null;
                    repaint();
                }
            }
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            Node foundNode = findNode(e.getPoint());
            if (foundNode != null) {
                if (deletingNode) {
                    edges.removeIf(edge -> edge.connects(foundNode));
                    nodes.remove(foundNode);
                    repaint();
                } else {
                    selectedNode = foundNode;
                }
            } else if (!deletingNode) {
                //Node labeling
                String label = JOptionPane.showInputDialog(this, "Enter label for the node:");
                if (label != null && !label.isEmpty()) {
                    Node newNode = new Node(e.getX(), e.getY(), 20, label);
                    nodes.add(newNode);
                    repaint();
                }
            }
        }
    }

    private Node findNode(Point p) {
        for (Node node : nodes) {
            if (node.containsPoint(p)) {
                return node;
            }
        }
        return null;
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}

    class Node {
        int x, y, radius;
        String label;
        int index;

        public Node(int x, int y, int radius, String label) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.label = label;
            this.index = nodes.size(); // Assign index based on the position in the nodes list
        }

        public void draw(Graphics g) {
            g.setColor(Color.WHITE);
            g.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
            g.setColor(Color.BLACK);
            g.drawOval(x - radius, y - radius, 2 * radius, 2 * radius); // Draw only the outline
            g.drawString(label, x - 5, y + 5); // Draw label near the node
        }

        public boolean containsPoint(Point p) {
            int dx = x - p.x;
            int dy = y - p.y;
            return dx * dx + dy * dy <= radius * radius;
        }
    }

    class Edge {
        Node start, end;

        public Edge(Node start, Node end) {
            this.start = start;
            this.end = end;
        }

        // Draw an edge from one outer node circumference to another
        public void draw(Graphics g) {
            g.setColor(Color.BLACK);
            // Calculate the coordinates for the edges to start and end at the circumference of the nodes
            int startX = start.x + (int) (Math.cos(getAngle()) * start.radius);
            int startY = start.y + (int) (Math.sin(getAngle()) * start.radius);
            int endX = end.x + (int) (Math.cos(getAngle()) * end.radius);
            int endY = end.y + (int) (Math.sin(getAngle()) * end.radius);
            g.drawLine(startX, startY, endX, endY);
        }

        // Calculate the angle of the line connecting the start and end nodes
        private double getAngle() {
            return Math.atan2(end.y - start.y, end.x - start.x);
        }

        public boolean connects(Node node) {
            return start == node || end == node;
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Hamiltonian Cycle Detector");
            ImageIcon logo = new ImageIcon("Hamiltonian.png");
            frame.setIconImage(logo.getImage());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new GraphPanel());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

